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
 * <p>
 * Note1:
 * https://medium.com/@haydar_ai/better-way-to-get-the-item-position-in-androids-recyclerview-820667d435d4
 * According to this article, we should use holder.getAdapterPosition() to retrieve the item's position in action event
 * For example: click event
 * <p>
 * Note2:
 * https://android.jlelse.eu/anatomy-of-recyclerview-part-1-a-search-for-a-viewholder-404ba3453714
 * According to this one, RecycleView implement cache and poll for initiating view holder under the hood.
 * So be-careful when user user set holder items value without binding via data
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
        protected static final int VIEW_TYPE_LOADING = -1;

        // dataset
        private List<T> mDataset;

        // custom layout/views
        private int _customLoadingLayout;

        // reference to previous and current selected item
        protected ItemHolder<T> mPreSelectedItem;
        protected ItemHolder<T> mSelectedItem;

        // listener
        private OnItemClickListener<T> mItemClickListener;
        private OnItemLongClickListener<T> mItemLongClickListener;
        private OnItemDoubleClickListener<T> mItemDoubleClickListener;
        private OnItemSelectionListener<T> mItemSelectionListener = new OnItemSelectionListener<T>() {
            @Override
            public boolean onSelection(@NonNull ViewHolder<T> viewHolder) {
                return onMiddlewareItemSelection(viewHolder);
            }
        };
        private OnItemTouchDownListener<T> mItemTouchDownListener;
        private OnItemTouchUpListener<T> mItemTouchUpListener;

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
            holder.bindData(getData(position));
            holder.bindClickListener(mItemClickListener);
            holder.bindLongClickListener(mItemLongClickListener);
            holder.bindDoubleClickListener(mItemDoubleClickListener);
            holder.bindItemSelection(mItemSelectionListener);
            holder.bindItemTouchDownListener(mItemTouchDownListener);
            holder.bindItemTouchUpListener(mItemTouchUpListener);
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

        /* ---------------------- ABSTRACT -----------------------*/

        /**
         * On item selection
         *
         * @param viewHolder item holder which hold data and respective position
         * @return true if the listener has consumed the event, false otherwise.
         */
        protected boolean onMiddlewareItemSelection(@NonNull ViewHolder<T> viewHolder) {
            mPreSelectedItem = mSelectedItem;
            mSelectedItem = new ItemHolder<>(viewHolder.getAdapterPosition(), viewHolder.getAdapterData());
            return false;
        }

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
            mPreSelectedItem = null;
            mSelectedItem = null;
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

            // update position of previous and current selected items
            if (mPreSelectedItem != null && mPreSelectedItem.getPosition() >= position) {
                mPreSelectedItem.mPosition += 1;
            }
            if (mSelectedItem != null && mSelectedItem.getPosition() >= position) {
                mSelectedItem.mPosition += 1;
            }
        }

        /**
         * Add new item at the end of list
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
         * @return removed data
         */
        public T removeData(T data) {
            int position = mDataset.indexOf(data);
            return removeData(position);
        }

        /**
         * Remove an item at given position
         *
         * @param position item's position
         */
        public T removeData(int position) {
            T data = mDataset.get(position);
            mDataset.remove(position);
            notifyItemRemoved(position);

            // update position of previous and current selected items
            if (mPreSelectedItem != null) {
                if (mPreSelectedItem.getPosition() == position) {
                    mPreSelectedItem = null;
                } else if (mPreSelectedItem.getPosition() > position) {
                    mPreSelectedItem.mPosition -= 1;
                }
            }
            if (mSelectedItem != null) {
                if (mSelectedItem.getPosition() == position) {
                    mSelectedItem = null;
                } else if (mSelectedItem.getPosition() > position) {
                    mSelectedItem.mPosition -= 1;
                }
            }
            return data;
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
        private void addLoading() {
            addData(null);
        }

        /**
         * Remove loading item
         */
        private void removeLoading() {
            int size = mDataset.size();
            if (size > 0 && mDataset.get(size - 1) == null) {
                mDataset.remove(size - 1);
                notifyItemRemoved(size);
            }
        }

        /**
         * @return selected item
         */
        @Nullable
        public ItemHolder<T> getSelectedItem() {
            return mSelectedItem;
        }

        /**
         * @return previous selected item
         */
        @Nullable
        public ItemHolder<T> getPreSelectedItem() {
            return mPreSelectedItem;
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

        /**
         * Set on item touch down listener
         *
         * @param listener listener
         * @return adapter
         */
        public Adapter<T> setOnItemTouchDownListener(OnItemTouchDownListener<T> listener) {
            mItemTouchDownListener = listener;
            return this;
        }

        /**
         * Set on item touch up listener
         *
         * @param listener listener
         * @return adapter
         */
        public Adapter<T> setOnItemTouchUpListener(OnItemTouchUpListener<T> listener) {
            mItemTouchUpListener = listener;
            return this;
        }

        /* ---------------------- METHOD ------------------------- */

        /* ---------------------- INNER CLASS -------------------- */

        /**
         * Item selection listener (click/double click/long click);
         *
         * @param <T> data model
         */
        private interface OnItemSelectionListener<T> {
            /**
             * trigger when user select an item
             *
             * @param viewHolder view holder
             * @return true if the listener has consumed the event, false otherwise.
             */
            boolean onSelection(@NonNull ViewHolder<T> viewHolder);
        }

        /**
         * On item click listener
         *
         * @param <T> data model
         */
        public interface OnItemClickListener<T> {
            /**
             * Trigger when user click on item view
             *
             * @param viewHolder item view holder
             * @param view       clicked view
             * @param data       data
             */
            void onClick(ViewHolder<T> viewHolder, View view, T data);
        }

        /**
         * On item long click listener
         *
         * @param <T> data model
         */
        public interface OnItemLongClickListener<T> {
            /**
             * Trigger when user long press on item view
             *
             * @param viewHolder item view holder
             * @param view       clicked view
             * @param data       data
             */
            void onLongClick(ViewHolder<T> viewHolder, View view, T data);
        }

        /**
         * On item double click listener
         *
         * @param <T> data model
         */
        public interface OnItemDoubleClickListener<T> {
            /**
             * Trigger when user double click on item view
             *
             * @param viewHolder item view holder
             * @param view       clicked view
             * @param data       data
             */
            void onDoubleClick(ViewHolder<T> viewHolder, View view, T data);
        }

        /**
         * On mouse down listener
         *
         * @param <T> data model
         */
        public interface OnItemTouchDownListener<T> {
            /**
             * Trigger when user touch down on itm
             *
             * @param viewHolder item view holder
             * @param view       clicked view
             * @param data       data
             */
            void onTouchDown(ViewHolder<T> viewHolder, View view, T data);
        }

        /**
         * On mouse up listener
         *
         * @param <T> data model
         */
        public interface OnItemTouchUpListener<T> {
            /**
             * Trigger when user touch up on itm
             *
             * @param viewHolder item view holder
             * @param view       clicked view
             * @param data       data
             */
            void onTouchUp(ViewHolder<T> viewHolder, View view, T data);
        }
    }

    /**
     * ViewHolder wrapper
     */
    public static class ViewHolder<T> extends RecyclerView.ViewHolder {

        // touched view
        private View mView;

        // item's data
        private T mData;

        // item click listener
        private Adapter.OnItemSelectionListener<T> mItemSelectionListener;
        private Adapter.OnItemClickListener<T> mItemClickListener;
        private Adapter.OnItemLongClickListener<T> mItemLongClickListener;
        private Adapter.OnItemDoubleClickListener<T> mItemDoubleClickListener;
        private Adapter.OnItemTouchDownListener<T> mItemTouchDownListener;
        private Adapter.OnItemTouchUpListener<T> mItemTouchUpListener;
        private GestureDetector mGestureDetector = new GestureDetector(itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                if (mItemSelectionListener.onSelection(ViewHolder.this)) {
                    return;
                }
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onLongClick(ViewHolder.this, mView, getAdapterData());
                }
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (mItemSelectionListener.onSelection(ViewHolder.this)) {
                    return true;
                }
                if (mItemDoubleClickListener != null) {
                    mItemDoubleClickListener.onDoubleClick(ViewHolder.this, mView, getAdapterData());
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mItemSelectionListener.onSelection(ViewHolder.this)) {
                    return true;
                }
                if (mItemClickListener != null) {
                    mItemClickListener.onClick(ViewHolder.this, mView, getAdapterData());
                }
                return true;
            }
        });

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            itemView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    v.performClick();
                    mView = v;
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            if (mItemTouchDownListener != null) {
                                mItemTouchDownListener.onTouchDown(ViewHolder.this, mView, getAdapterData());
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            if (mItemTouchUpListener != null) {
                                mItemTouchUpListener.onTouchUp(ViewHolder.this, mView, getAdapterData());
                            }
                            break;
                    }
                    mGestureDetector.onTouchEvent(event);
                    return true;
                }
            });
        }

        /**
         * Get adapter data
         *
         * @return data
         */
        @NonNull
        public final T getAdapterData() {
            return mData;
        }

        /**
         * Bind data
         *
         * @param data data
         */
        protected void bindData(T data) {
            mData = data;
        }

        /**
         * Bind item click listener from adapter
         *
         * @param listener item click listener
         */
        private void bindClickListener(Adapter.OnItemClickListener<T> listener) {
            mItemClickListener = listener;
        }

        /**
         * Bind item long click listener from adapter
         *
         * @param listener item  long click listener
         */
        private void bindLongClickListener(Adapter.OnItemLongClickListener<T> listener) {
            mItemLongClickListener = listener;
        }

        /**
         * Bind item double click listener from adapter
         *
         * @param listener item  double click listener
         */
        private void bindDoubleClickListener(Adapter.OnItemDoubleClickListener<T> listener) {
            mItemDoubleClickListener = listener;
        }

        /**
         * Bind item selection listener (click, double click, long click)
         *
         * @param listener on item selection listener
         */
        private void bindItemSelection(Adapter.OnItemSelectionListener<T> listener) {
            mItemSelectionListener = listener;
        }

        /**
         * Bind item touch down listener
         *
         * @param listener on item touch down listener
         */
        private void bindItemTouchDownListener(Adapter.OnItemTouchDownListener<T> listener) {
            mItemTouchDownListener = listener;
        }

        /**
         * Bind item touch up listener
         *
         * @param listener on item touch down listener
         */
        private void bindItemTouchUpListener(Adapter.OnItemTouchUpListener<T> listener) {
            mItemTouchUpListener = listener;
        }
    }

    /**
     * Loading view holder
     */
    private static class LoadingViewHolder<T> extends ViewHolder<T> {

        private LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * Data holder which hold data's position and value
     *
     * @param <T> data model
     */
    public static class ItemHolder<T> {
        // position of data in list
        private int mPosition;

        // data
        private T mData;

        private ItemHolder(int position, T data) {
            mPosition = position;
            mData = data;
        }

        /**
         * @return data's position in list
         */
        public int getPosition() {
            return mPosition;
        }

        /**
         * @return data value
         */
        public T getData() {
            return mData;
        }
    }
}
