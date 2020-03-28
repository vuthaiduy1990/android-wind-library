package the.wind.library.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import the.wind.library.CWHandler;
import the.wind.library.R;

/**
 * RecycleView Wrapper
 */
public class WindRecycleView extends RecyclerView {

    public static final int LOADMORE_THRESHOLD = 10;

    // threshold. (number of item before scrolling to end)
    // trigger loadmore before scrolling go nearly to end
    private int mThreshold = LOADMORE_THRESHOLD;

    // loading sate
    private boolean isLoading = false;

    // Listener
    private OnLoadMoreListener mLoadMoreListener;

    public WindRecycleView(@NonNull Context context) {
        this(context, null);
    }

    public WindRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WindRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // set default layout manager
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        // listen the scroll event and handle loadmore
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LayoutManager layoutManager = getLayoutManager();

                // do not handle loadmore event
                if (mLoadMoreListener == null || layoutManager == null) return;
                if (isLoading) return;

                int lastVisibleItemPos = 0;
                if (layoutManager instanceof StaggeredGridLayoutManager) {
                    int[] itemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
                    // get the last element which have max position within the list
                    lastVisibleItemPos = getLastVisibleItemPosition(itemPositions);
                } else if (layoutManager instanceof GridLayoutManager) {
                    lastVisibleItemPos = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                } else if (layoutManager instanceof LinearLayoutManager) {
                    lastVisibleItemPos = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                }

                // do loading before user scroll nearly to the end
                if (layoutManager.getItemCount() <= lastVisibleItemPos + mThreshold) {
                    // add null data which represent for a loading item view
                    if (getAdapter() instanceof Adapter) {
                        ((Adapter) getAdapter()).addLoading();
                    }
                    // handle loadmore
                    mLoadMoreListener.onLoadMore(new CWHandler<Void>() {
                        @Override
                        public void onBefore(Void... params) {
                            super.onBefore(params);
                            // before adding more data to adapter, we should remove the added null value above
                            if (getAdapter() instanceof Adapter) {
                                ((Adapter) getAdapter()).removeLoading();
                            }
                        }

                        @Override
                        public void onHandle(Void... params) {
                            // do nothing
                        }

                        @Override
                        public void onAfter(Void... params) {
                            super.onAfter(params);
                            cancelLoading();
                        }
                    });
                    isLoading = true;
                }
            }
        });
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public void setLayoutManager(@Nullable final LayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout instanceof GridLayoutManager) {
            ((GridLayoutManager) layout).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    RecyclerView.Adapter adapter = getAdapter();
                    // for loading view -> span over all column
                    // so that the loading view will be at center
                    if (adapter instanceof Adapter && adapter.getItemViewType(position) == Adapter.VIEW_TYPE_LOADING) {
                        return ((GridLayoutManager) layout).getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT -----------------------*/

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Set threshold (number of item before to end)
     * When user scroll items nearly end, for example 5 items left
     * The recycle view will trigger loadmore event
     *
     * @param threshold threshold
     * @return recycle view
     */
    public RecyclerView setThreshold(int threshold) {
        mThreshold = threshold;
        return this;
    }

    /**
     * Notify that the more data has been loaded
     */
    public void cancelLoading() {
        isLoading = false;
    }

    /**
     * Set on loadmore listener
     *
     * @param listener listener
     * @return recycle view
     */
    public RecyclerView setOnLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
        return this;
    }

    /**
     * Judge the last visible item position
     *
     * @param itemPositions list of item's positions
     * @return the last position
     */
    private int getLastVisibleItemPosition(int[] itemPositions) {
        int max = 0;
        for (int pos : itemPositions) {
            if (pos >= max) {
                max = pos;
            }
        }
        return max;
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * On load more listener
     */
    public interface OnLoadMoreListener {
        /**
         * Trigger when user scroll to end of recycle view
         *
         * @param handler handler
         */
        void onLoadMore(CWHandler<Void> handler);
    }

    /**
     * Adapter wrapper
     *
     * @param <T> View Holder
     */
    public abstract static class Adapter<T> extends RecyclerView.Adapter<ViewHolder<T>> {

        // view type
        protected static final int VIEW_TYPE_ITEM = 0;
        protected static final int VIEW_TYPE_LOADING = 1;

        // dataset
        private List<T> mDataset;

        // custom layout/views
        private int _customLoadingLayout;

        // listener
        private OnItemClickListener<T> mItemClickListener;
        private OnItemLongClickListener<T> mItemLongClickListener;
        private OnItemDoubleClickListener<T> mItemDoubleClickListener;

        /**
         * Constructor
         *
         * @param dataset data
         */
        public Adapter(List<T> dataset) {
            mDataset = dataset;
            setCustomLoadingView(R.layout.recycle_view_loading);
        }

        /* ---------------------- OVERRIDE ----------------------- */

        @SuppressWarnings("ConstantConditions")
        @NonNull
        @Override
        public ViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(parent.getContext()).inflate(_customLoadingLayout, parent, false);
                return new LoadingViewHolder<>(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder<T> holder, int position) {
            holder.bindClickListener(mItemClickListener, mDataset.get(position), position);
            holder.bindLongClickListener(mItemLongClickListener, mDataset.get(position), position);
            holder.bindDoubleClickListener(mItemDoubleClickListener, mDataset.get(position), position);
        }

        @Override
        public int getItemViewType(int position) {
            if (mDataset.get(position) == null) {
                return VIEW_TYPE_LOADING;
            }
            return VIEW_TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            return mDataset != null ? mDataset.size() : 0;
        }

        /* ---------------------- STATIC ------------------------- */

        /* ---------------------- GET-SET ------------------------ */

        /**
         * @return dataset
         */
        public List<T> getData() {
            return mDataset;
        }

        /**
         * Get data by position
         *
         * @param position item position
         * @return data
         */
        public T getData(int position) {
            return mDataset.get(position);
        }

        /**
         * Set data
         *
         * @param dataset dataset
         */
        public void setData(List<T> dataset) {
            mDataset = dataset;
            notifyDataSetChanged();
        }

        /**
         * Add new item to given position
         *
         * @param position position
         * @param data     item data
         */
        public void addData(int position, T data) {
            mDataset.add(position, data);
            notifyItemInserted(position);
        }

        /**
         * Add new item
         *
         * @param data item data
         */
        public void addData(T data) {
            mDataset.add(data);
            notifyItemInserted(mDataset.size() - 1);
        }

        /**
         * Update data
         *
         * @param position position
         * @param data     data item
         */
        public void updateData(int position, T data) {
            mDataset.set(position, data);
            notifyItemChanged(position);
        }

        /**
         * Remove an item
         *
         * @param data item data
         */
        public void removeData(T data) {
            int position = mDataset.indexOf(data);
            mDataset.remove(data);
            notifyItemRemoved(position);
        }

        /**
         * Clear all dataset
         */
        public void clearData() {
            mDataset.clear();
            notifyDataSetChanged();
        }

        /**
         * Add loading item
         */
        public void addLoading() {
            addData(null);
        }

        /**
         * Remove loading item
         */
        public void removeLoading() {
            int size = mDataset.size();
            if (size > 0 && mDataset.get(size - 1) == null) {
                mDataset.remove(size - 1);
                notifyItemRemoved(size);
            }
        }

        /**
         * Set custom loading view
         *
         * @param layoutId loading resource layout id
         * @return adapter
         */
        public Adapter<T> setCustomLoadingView(int layoutId) {
            _customLoadingLayout = layoutId;
            return this;
        }

        /**
         * Set on item click listener
         *
         * @param listener listener
         * @return adapter
         */
        public Adapter<T> setOnItemClickListener(OnItemClickListener<T> listener) {
            mItemClickListener = listener;
            return this;
        }

        /**
         * Set on item long click listener
         *
         * @param listener listener
         * @return adapter
         */
        public Adapter<T> setOnItemLongClickListener(OnItemLongClickListener<T> listener) {
            mItemLongClickListener = listener;
            return this;
        }

        /**
         * Set on item long click listener
         *
         * @param listener listener
         * @return adapter
         */
        public Adapter<T> setOnItemDoubleClickListener(OnItemDoubleClickListener<T> listener) {
            mItemDoubleClickListener = listener;
            return this;
        }

        /* ---------------------- METHOD ------------------------- */

        /* ---------------------- INNER CLASS -------------------- */

        /**
         * On item click listener
         */
        public interface OnItemClickListener<T> {
            /**
             * Trigger when user click on item view
             *
             * @param itemView item view layout
             * @param view     clicked view
             * @param data     data
             * @param position item's position
             */
            void onClick(View itemView, View view, T data, int position);
        }

        /**
         * On item long click listener
         */
        public interface OnItemLongClickListener<T> {
            /**
             * Trigger when user long press on item view
             *
             * @param itemView item view layout
             * @param view     clicked view
             * @param data     data
             * @param position item's position
             */
            void onLongClick(View itemView, View view, T data, int position);
        }

        /**
         * On item double click listener
         */
        public interface OnItemDoubleClickListener<T> {
            /**
             * Trigger when user double click on item view
             *
             * @param itemView item view layout
             * @param view     clicked view
             * @param data     data
             * @param position item's position
             */
            void onDoubleClick(View itemView, View view, T data, int position);
        }
    }

    /**
     * ViewHolder wrapper
     */
    public static class ViewHolder<T> extends RecyclerView.ViewHolder {

        // touched view
        private View mView;

        // item click listener
        private Adapter.OnItemClickListener<T> mItemClickListener;
        private Adapter.OnItemLongClickListener<T> mItemLongClickListener;
        private Adapter.OnItemDoubleClickListener<T> mItemDoubleClickListener;
        private GestureDetector mGestureDetector = new GestureDetector(itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onLongClick(itemView, mView, mData, mPosition);
                }
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mItemDoubleClickListener != null) {
                    mItemDoubleClickListener.onDoubleClick(itemView, mView, mData, mPosition);
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mItemClickListener != null) {
                    mItemClickListener.onClick(itemView, mView, mData, mPosition);
                }
                return true;
            }
        });

        // data model
        private T mData;
        private int mPosition;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            itemView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.performClick();
                    mView = v;
                    mGestureDetector.onTouchEvent(event);
                    return true;
                }
            });
        }


        /**
         * Bind item click listener from adapter
         *
         * @param listener item click listener
         * @param data     item data
         * @param position item's position
         */
        public void bindClickListener(Adapter.OnItemClickListener<T> listener, T data, int position) {
            mData = data;
            mPosition = position;
            mItemClickListener = listener;
        }

        /**
         * Bind item long click listener from adapter
         *
         * @param listener item  long click listener
         * @param data     item data
         * @param position item's position
         */
        public void bindLongClickListener(Adapter.OnItemLongClickListener<T> listener, T data, int position) {
            mData = data;
            mPosition = position;
            mItemLongClickListener = listener;
        }

        /**
         * Bind item double click listener from adapter
         *
         * @param listener item  double click listener
         * @param data     item data
         * @param position item's position
         */
        public void bindDoubleClickListener(Adapter.OnItemDoubleClickListener<T> listener, T data, int position) {
            mData = data;
            mPosition = position;
            mItemDoubleClickListener = listener;
        }
    }

    /**
     * Loading view holder
     */
    private static class LoadingViewHolder<T> extends ViewHolder<T> {

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
