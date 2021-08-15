package the.wind.library.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import the.wind.library.CWBundle;
import the.wind.library.CWHandler;
import the.wind.library.R;
import the.wind.library.utils.CWImageUtils;

/**
 * RecycleView Wrapper
 * <p>
 * Note1:
 * https://medium.com/@haydar_ai/better-way-to-get-the-item-position-in-androids-recyclerview-820667d435d4
 * According to this article, we should use holder.getBindingAdapterPosition() to retrieve the item's position in action event
 * For example: click event
 * <p>
 * Note2:
 * https://android.jlelse.eu/anatomy-of-recyclerview-part-1-a-search-for-a-viewholder-404ba3453714
 * According to this one, RecycleView implement cache and poll for initiating view holder under the hood.
 * So be-careful when user user set holder items value without binding via data
 */
public class WindRecycleView extends RecyclerView {

    public static final int LOADMORE_THRESHOLD = 10;
    private static final float SWIPE_WIDTH_RATIO = 1f / 5f;

    // threshold. (number of item before scrolling to end)
    // trigger loadmore before scrolling go nearly to end
    private int threshold = LOADMORE_THRESHOLD;

    // loading sate
    private boolean isLoading = false;

    // enable swipe to remove
    private boolean enableSwipeToRemove = false;

    // enable swap position
    private boolean enableSwapPosition = false;

    // left and right swipe to remove icon
    private Bitmap leftSwipeIcon;
    private Bitmap rightSwipeIcon;
    private float swipeIconSize;

    // Listener
    private OnLoadMoreListener loadMoreListener;

    // Swipe to remove listener
    private OnSwipeToRemoveListener swipeToRemoveListener;

    // Move position listener
    private OnSwapPositionListener swapPositionListener;

    // bundle data
    private final CWBundle bundle = new CWBundle();

    // Item touch helper
    private final ItemTouchHelper.Callback touchCallback = new ItemTouchHelper.Callback() {

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            if (enableSwapPosition) {
                Adapter<?> adapter = (Adapter<?>) getAdapter();
                if (adapter == null) return false;
                int fromPos = viewHolder.getBindingAdapterPosition();
                int toPos = target.getBindingAdapterPosition();
                if (fromPos < toPos) {
                    for (int i = fromPos; i < toPos; i++) {
                        Collections.swap(adapter.getData(), i, i + 1);
                    }
                } else {
                    for (int i = fromPos; i > toPos; i--) {
                        Collections.swap(adapter.getData(), i, i - 1);
                    }
                }
                adapter.notifyItemMoved(fromPos, toPos);
                if (swapPositionListener != null) {
                    swapPositionListener.onSwapped(recyclerView, viewHolder, target);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return enableSwapPosition;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return enableSwipeToRemove;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (swipeToRemoveListener != null) {
                swipeToRemoveListener.onSwiped(viewHolder, direction);
            }
        }

        @Override
        public void onChildDraw(
                @NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                float dx, float dy, int actionState, boolean isCurrentlyActive) {
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                View itemView = viewHolder.itemView;
                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                float verMargin = (height - swipeIconSize) / 2;
                float hozMargin = (dx * SWIPE_WIDTH_RATIO - swipeIconSize) / 2;

                if (dx < 0 && rightSwipeIcon != null) {
                    Paint p = new Paint();
                    // RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    // c.drawRect(background, p);
                    RectF iconDest = new RectF(
                            itemView.getRight() + hozMargin, itemView.getTop() + verMargin,
                            itemView.getRight() + hozMargin + swipeIconSize, itemView.getBottom() - verMargin);
                    c.drawBitmap(rightSwipeIcon, null, iconDest, p);
                } else if (dx > 0 && leftSwipeIcon != null) {
                    Paint p = new Paint();
                    RectF iconDest = new RectF(
                            hozMargin, itemView.getTop() + verMargin,
                            hozMargin + swipeIconSize, itemView.getBottom() - verMargin);
                    c.drawBitmap(leftSwipeIcon, null, iconDest, p);
                }
            } else {
                // c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }
            super.onChildDraw(c, recyclerView, viewHolder, dx * SWIPE_WIDTH_RATIO, dy, actionState, isCurrentlyActive);
        }
    };

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

        // set default animation
        setLayoutAnimation(LayoutAnim.SWEET_ALERT.getAnim(context));

        // Set default swipe icon
        leftSwipeIcon = CWImageUtils.drawbleToBitmap(context, R.drawable.wl_ic_trash);
        rightSwipeIcon = leftSwipeIcon;
        swipeIconSize = context.getResources().getDimension(R.dimen.wl_icon);

        // listen the scroll event and handle loadmore
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LayoutManager layoutManager = getLayoutManager();

                // do not handle loadmore event
                if (loadMoreListener == null || layoutManager == null) return;
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
                if (layoutManager.getItemCount() <= lastVisibleItemPos + threshold) {
                    // add null data which represent for a loading item view
                    if (getAdapter() instanceof Adapter) {
                        ((Adapter<?>) getAdapter()).addLoading();
                    }
                    // handle loadmore
                    loadMoreListener.onLoadMore(new CWHandler<Void>() {
                        @Override
                        public void onBefore(Void... params) {
                            super.onBefore(params);
                            // before adding more data to adapter, we should remove the added null value above
                            if (getAdapter() instanceof Adapter) {
                                ((Adapter<?>) getAdapter()).removeLoading();
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
                    RecyclerView.Adapter<?> adapter = getAdapter();
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

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return bundle data
     */
    public CWBundle bundle() {
        return bundle;
    }

    /**
     * Set threshold (number of item before to end)
     * When user scroll items nearly end, for example 5 items left
     * The recycle view will trigger loadmore event
     *
     * @param threshold threshold
     * @return recycle view
     */
    public RecyclerView setThreshold(int threshold) {
        this.threshold = threshold;
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
        loadMoreListener = listener;
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

    /**
     * Set left swipe icon
     *
     * @param bitmap bitmap
     */
    public void setLeftSwipeIcon(Bitmap bitmap) {
        this.leftSwipeIcon = bitmap;
    }

    /**
     * Set right swipe icon
     *
     * @param bitmap bitmap
     */
    public void setRightSwipeIcon(Bitmap bitmap) {
        this.rightSwipeIcon = bitmap;
    }

    /**
     * Set swipe icon size
     *
     * @param size size
     */
    public void setSwipeIconSize(float size) {
        this.swipeIconSize = size;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Enable swipe to remove function on recycle view
     *
     * @param listener swipe listener
     */
    public void enableSwipeToRemove(OnSwipeToRemoveListener listener) {
        enableSwipeToRemove = true;
        swipeToRemoveListener = listener;
        ItemTouchHelper helper = new ItemTouchHelper(touchCallback);
        helper.attachToRecyclerView(this);
    }

    /**
     * Enable swap item by moving item to new position
     *
     * @param listener swap listener
     */
    public void enableSwapPosition(OnSwapPositionListener listener) {
        enableSwapPosition = true;
        swapPositionListener = listener;
        ItemTouchHelper helper = new ItemTouchHelper(touchCallback);
        helper.attachToRecyclerView(this);
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Layout animation
     * Thanks https://proandroiddev.com/enter-animation-using-recyclerview-and-layoutanimation-part-1-list-75a874a5d213
     * Thanks https://github.com/pedant/sweet-alert-dialog
     */
    public enum LayoutAnim {
        SWEET_ALERT(R.anim.wl_recycle_view_item_anim_sweet_alert),
        FADE(R.anim.wl_recycle_view_item_anim_fade),
        LEFT_2_RIGHT(R.anim.wl_recycle_view_item_anim_left_right),
        RIGHT_2_LEFT(R.anim.wl_recycle_view_item_anim_right_left);

        private final int anim;

        LayoutAnim(int anim) {
            this.anim = anim;
        }

        /**
         * Get animation
         *
         * @param context application context
         * @return animation
         */
        public LayoutAnimationController getAnim(Context context) {
            LayoutAnimationController layoutAnim = AnimationUtils.loadLayoutAnimation(context, R.anim.wle_view_layout_anim);
            layoutAnim.setAnimation(context, anim);
            return layoutAnim;
        }

    }

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
     * On swipe to remove listener
     */
    public interface OnSwipeToRemoveListener {

        /**
         * Triggwe when user swipe item to left or right
         *
         * @param viewHolder view holder
         * @param direction  direction.
         * @see androidx.recyclerview.widget.ItemTouchHelper#LEFT
         * @see androidx.recyclerview.widget.ItemTouchHelper#RIGHT
         */
        void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction);
    }

    /**
     * On swap item position
     */
    public interface OnSwapPositionListener {
        /**
         * Trigger when user drag and drip item to change it's position
         *
         * @param recyclerView recycle view
         * @param viewHolder   The ViewHolder which is being dragged by the user.
         * @param target       target view holder where item is move to
         */
        void onSwapped(@NonNull RecyclerView recyclerView,
                       @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target);
    }

    /**
     * Adapter wrapper
     * Event trigger time and order
     * <pre>
     *      + Click event   : touch down -> touch up -> click event
     *      + Double click  : touch down -> touch up -> touch down -> double click -> touch up
     *      + Long click    : touch down -> long click -> touch up
     * </pre>
     *
     * @param <T> View Holder
     */
    public abstract static class Adapter<T> extends RecyclerView.Adapter<ViewHolder<T>> {

        // view type
        protected static final int VIEW_TYPE_ITEM = 0;
        protected static final int VIEW_TYPE_LOADING = -1;

        // dataset
        private List<T> dataset;

        // custom layout/views
        private int _customLoadingLayout;

        // reference to previous and current selected item
        private ItemHolder<T> mPreSelectedItem;
        private ItemHolder<T> mSelectedItem;

        // listener
        private OnItemClickListener<T> itemClickListener;
        private OnItemLongClickListener<T> itemLongClickListener;
        private OnItemDoubleClickListener<T> itemDoubleClickListener;
        private final OnItemSelectionListener<T> itemSelectionListener = new OnItemSelectionListener<T>() {
            @Override
            public boolean onSelection(@NonNull ViewHolder<T> viewHolder) {
                return onMiddlewareItemSelection(viewHolder);
            }
        };
        private OnItemTouchDownListener<T> itemTouchDownListener;
        private OnItemTouchUpListener<T> itemTouchUpListener;

        // bundle data
        private final CWBundle bundle = new CWBundle();

        /**
         * Constructor
         *
         * @param dataset data
         */
        public Adapter(List<T> dataset) {
            this.dataset = dataset;
            setCustomLoadingView(R.layout.wl_recycle_view_loading);

        }

        /* ---------------------- OVERRIDE ----------------------- */

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
            holder.bindClickListener(itemClickListener);
            holder.bindLongClickListener(itemLongClickListener);
            holder.bindDoubleClickListener(itemDoubleClickListener);
            holder.bindItemSelection(itemSelectionListener);
            holder.bindItemTouchDownListener(itemTouchDownListener);
            holder.bindItemTouchUpListener(itemTouchUpListener);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder<T> holder, int position, @NonNull List<Object> payloads) {
            this.onBindViewHolder(holder, position);
        }

        @Override
        public int getItemViewType(int position) {
            if (dataset.get(position) == null) {
                return VIEW_TYPE_LOADING;
            }
            return VIEW_TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            return dataset != null ? dataset.size() : 0;
        }

        /* ---------------------- STATIC ------------------------- */

        /* ---------------------- ABSTRACT ----------------------- */

        /**
         * On item selection
         *
         * @param viewHolder item holder which hold data and respective position
         * @return true if the listener has consumed the event, false otherwise.
         */
        protected boolean onMiddlewareItemSelection(@NonNull ViewHolder<T> viewHolder) {
            mPreSelectedItem = mSelectedItem;
            mSelectedItem = new ItemHolder<>(viewHolder.getBindingAdapterPosition(), viewHolder.getAdapterData());
            return false;
        }

        /* ---------------------- GET-SET ------------------------ */

        /**
         * @return bundle data
         */
        public CWBundle bundle() {
            return bundle;
        }

        /**
         * @return dataset
         */
        public List<T> getData() {
            return dataset;
        }

        /**
         * Set data
         *
         * @param dataset dataset
         */
        public void setData(List<T> dataset) {
            mPreSelectedItem = null;
            mSelectedItem = null;
            this.dataset = dataset;
            notifyDataSetChanged();
        }

        /**
         * Get data by position
         *
         * @param position item position
         * @return data
         */
        public T getData(int position) {
            return dataset.get(position);
        }

        /**
         * Add new item to given position
         *
         * @param position position
         * @param data     item data
         */
        public void addData(int position, T data) {
            dataset.add(position, data);
            notifyItemInserted(position);

            // update position of previous and current selected items
            if (mPreSelectedItem != null && mPreSelectedItem.getPosition() >= position) {
                mPreSelectedItem.position += 1;
            }
            if (mSelectedItem != null && mSelectedItem.getPosition() >= position) {
                mSelectedItem.position += 1;
            }
        }

        /**
         * Add new item at the end of list
         *
         * @param data item data
         */
        public void addData(T data) {
            dataset.add(data);
            notifyItemInserted(dataset.size() - 1);
        }

        /**
         * Update data
         *
         * @param position position
         * @param data     data item
         */
        public void updateData(int position, T data) {
            dataset.set(position, data);
            notifyItemChanged(position);
        }

        /**
         * Remove an item
         *
         * @param data item data
         * @return removed data
         */
        public T removeData(T data) {
            int position = dataset.indexOf(data);
            return removeData(position);
        }

        /**
         * Remove an item at given position
         *
         * @param position item's position
         */
        public T removeData(int position) {
            T data = dataset.get(position);
            dataset.remove(position);
            notifyItemRemoved(position);

            // update position of previous and current selected items
            if (mPreSelectedItem != null) {
                if (mPreSelectedItem.getPosition() == position) {
                    mPreSelectedItem = null;
                } else if (mPreSelectedItem.getPosition() > position) {
                    mPreSelectedItem.position -= 1;
                }
            }
            if (mSelectedItem != null) {
                if (mSelectedItem.getPosition() == position) {
                    mSelectedItem = null;
                } else if (mSelectedItem.getPosition() > position) {
                    mSelectedItem.position -= 1;
                }
            }
            return data;
        }

        /**
         * Clear all dataset
         */
        public void clearData() {
            dataset.clear();
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
            int size = dataset.size();
            if (size > 0 && dataset.get(size - 1) == null) {
                dataset.remove(size - 1);
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
         * Clear selected item reference
         */
        public void clearSelectedItemRef() {
            mSelectedItem = null;
        }

        /**
         * @return previous selected item
         */
        @Nullable
        public ItemHolder<T> getPreSelectedItem() {
            return mPreSelectedItem;
        }

        /**
         * Clear previous selected item reference
         */
        public void clearPreSelectedItemRef() {
            mPreSelectedItem = null;
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
            itemClickListener = listener;
            return this;
        }

        /**
         * Set on item long click listener
         *
         * @param listener listener
         * @return adapter
         */
        public Adapter<T> setOnItemLongClickListener(OnItemLongClickListener<T> listener) {
            itemLongClickListener = listener;
            return this;
        }

        /**
         * Set on item long click listener
         *
         * @param listener listener
         * @return adapter
         */
        public Adapter<T> setOnItemDoubleClickListener(OnItemDoubleClickListener<T> listener) {
            itemDoubleClickListener = listener;
            return this;
        }

        /**
         * Set on item touch down listener
         *
         * @param listener listener
         * @return adapter
         */
        public Adapter<T> setOnItemTouchDownListener(OnItemTouchDownListener<T> listener) {
            itemTouchDownListener = listener;
            return this;
        }

        /**
         * Set on item touch up listener
         *
         * @param listener listener
         * @return adapter
         */
        public Adapter<T> setOnItemTouchUpListener(OnItemTouchUpListener<T> listener) {
            itemTouchUpListener = listener;
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
        private View touchedView;

        // item's data
        private T data;

        // item click listener
        private Adapter.OnItemSelectionListener<T> itemSelectionListener;
        private Adapter.OnItemClickListener<T> itemClickListener;
        private Adapter.OnItemLongClickListener<T> itemLongClickListener;
        private Adapter.OnItemDoubleClickListener<T> itemDoubleClickListener;
        private Adapter.OnItemTouchDownListener<T> itemTouchDownListener;
        private Adapter.OnItemTouchUpListener<T> itemTouchUpListener;
        private final GestureDetector gestureDetector = new GestureDetector(itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                if (itemSelectionListener.onSelection(ViewHolder.this)) {
                    return;
                }
                if (itemLongClickListener != null) {
                    itemLongClickListener.onLongClick(ViewHolder.this, touchedView, getAdapterData());
                }
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (itemSelectionListener.onSelection(ViewHolder.this)) {
                    return true;
                }
                if (itemDoubleClickListener != null) {
                    itemDoubleClickListener.onDoubleClick(ViewHolder.this, touchedView, getAdapterData());
                }
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (itemSelectionListener.onSelection(ViewHolder.this)) {
                    return true;
                }
                if (itemClickListener != null) {
                    itemClickListener.onClick(ViewHolder.this, touchedView, getAdapterData());
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
                    touchedView = v;
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            if (itemTouchDownListener != null) {
                                itemTouchDownListener.onTouchDown(ViewHolder.this, touchedView, getAdapterData());
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            if (itemTouchUpListener != null) {
                                itemTouchUpListener.onTouchUp(ViewHolder.this, touchedView, getAdapterData());
                            }
                            break;
                    }
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });
        }

        /**
         * @return application context
         */
        public Context getContext() {
            return itemView.getContext();
        }

        /**
         * Get adapter data
         *
         * @return data
         */
        @NonNull
        public final T getAdapterData() {
            return data;
        }

        /**
         * Bind data
         *
         * @param data data
         */
        protected void bindData(T data) {
            this.data = data;
        }

        /**
         * Bind item click listener from adapter
         *
         * @param listener item click listener
         */
        private void bindClickListener(Adapter.OnItemClickListener<T> listener) {
            itemClickListener = listener;
        }

        /**
         * Bind item long click listener from adapter
         *
         * @param listener item  long click listener
         */
        private void bindLongClickListener(Adapter.OnItemLongClickListener<T> listener) {
            itemLongClickListener = listener;
        }

        /**
         * Bind item double click listener from adapter
         *
         * @param listener item  double click listener
         */
        private void bindDoubleClickListener(Adapter.OnItemDoubleClickListener<T> listener) {
            itemDoubleClickListener = listener;
        }

        /**
         * Bind item selection listener (click, double click, long click)
         *
         * @param listener on item selection listener
         */
        private void bindItemSelection(Adapter.OnItemSelectionListener<T> listener) {
            itemSelectionListener = listener;
        }

        /**
         * Bind item touch down listener
         *
         * @param listener on item touch down listener
         */
        private void bindItemTouchDownListener(Adapter.OnItemTouchDownListener<T> listener) {
            itemTouchDownListener = listener;
        }

        /**
         * Bind item touch up listener
         *
         * @param listener on item touch down listener
         */
        private void bindItemTouchUpListener(Adapter.OnItemTouchUpListener<T> listener) {
            itemTouchUpListener = listener;
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
        private int position;

        // data
        private final T data;

        private ItemHolder(int position, T data) {
            this.position = position;
            this.data = data;
        }

        /**
         * @return data's position in list
         */
        public int getPosition() {
            return position;
        }

        /**
         * @return data value
         */
        public T getData() {
            return data;
        }
    }
}
