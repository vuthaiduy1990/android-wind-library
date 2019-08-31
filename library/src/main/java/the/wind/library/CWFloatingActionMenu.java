package the.wind.library;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class CWFloatingActionMenu extends RelativeLayout {

    private final int DEFAULT_ANIMATE_INTERVAL = 100; // milliseconds

    // Views and it's setting
    private FloatingActionButton _fbMenuBtn;
    private ViewGroup _itemHolder;
    private float mScale = 1f; // auto adjust size based on dp
    private boolean mOpening = false;

    // Animation
    private ScaleAnimation mfbMenuBtnAnim;
    private OpeningAnimation mOpeningAnim;
    private ClosingAnimation mClosingAnim;

    // models
    private List<MenuItem> mMenuItems = new LinkedList<>();
    private CWTag mTag = new CWTag();
    private OnStateListener mStateListener;

    public CWFloatingActionMenu(Context context) {
        this(context, null, 0);
    }

    public CWFloatingActionMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CWFloatingActionMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.floating_action_menu, this, true);

            // bind views
            _itemHolder = findViewById(R.id.itemHolder);
            _fbMenuBtn = findViewById(R.id.fbMenuBtn);
            _fbMenuBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggle(true);
                }
            });

            // create scale animation
            mfbMenuBtnAnim = new ScaleAnimation(
                    1f, 1.3f, 1f, 1.3f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );
            mfbMenuBtnAnim.setRepeatMode(Animation.RESTART);
            mfbMenuBtnAnim.setRepeatCount(Animation.INFINITE);
            mfbMenuBtnAnim.setDuration(800);

            // opening and closing animation
            mOpeningAnim = new OpeningAnimation(this);
            mClosingAnim = new ClosingAnimation(this);
        }
    }

    /* ---------------------- EVENT -------------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return tagging model
     */
    public CWTag tag() {
        return mTag;
    }

    /**
     * @return list of menu items
     */
    public Set<String> menuItemKeys() {
        Set<String> keys = new LinkedHashSet<>();
        for (MenuItem item : mMenuItems) {
            keys.add(item.key());
        }
        return keys;
    }

    /**
     * Get item by key
     *
     * @param key key
     * @return action menu
     */
    @Nullable
    public MenuItem getMenuItem(String key) {
        for (MenuItem item : mMenuItems) {
            if (item.key().equals(key)) return item;
        }
        return null;
    }

    /**
     * Add menu item
     *
     * @param item menu item
     * @return action menu
     */
    private CWFloatingActionMenu addItem(MenuItem item) {
        item.setVisibility(GONE);
        item.parent(this);
        mMenuItems.add(item);

        // https://material.io/design/components/buttons-floating-action-button.html#
        // 40 is mini size of floating action button
        int itemSize = toDip(40, mScale);
        item.button().setLayoutParams(new LinearLayout.LayoutParams(itemSize, itemSize));

        // add item to action menu
        _itemHolder.addView(item, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        return this;
    }

    /**
     * Clear all menu Item
     *
     * @return action menu
     */
    public CWFloatingActionMenu clearItems() {
        mMenuItems.clear();
        _itemHolder.removeAllViews();
        return this;
    }

    /**
     * @return action menu button
     */
    public FloatingActionButton button() {
        return _fbMenuBtn;
    }

    /**
     * Automatic scale icon based on resolution of devices
     * Note: we should set scale before adding new items to parent
     *
     * @param scale size
     */
    public void setResolutionScale(float scale) {
        mScale = scale;

        // https://material.io/design/components/buttons-floating-action-button.html#
        // 56dp is normal size of floating action button
        int menuBtnSize = toDip(56, scale);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(menuBtnSize, menuBtnSize);
        int margin = toDip(12, scale);
        params.setMargins(margin, margin, margin, margin);
        _fbMenuBtn.setLayoutParams(params);
    }

    /**
     * Scale given dp size
     *
     * @param dpSize unit is dp
     * @param scale  scale number
     * @return scaled size
     */
    private int toDip(int dpSize, float scale) {
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dpSize,
                getResources().getDisplayMetrics()) * scale);
    }

    /**
     * @return state listener
     */
    public OnStateListener getStateListener() {
        return mStateListener;
    }

    /**
     * Set state change listener
     *
     * @param listener listener
     * @return action menu
     */
    public CWFloatingActionMenu setStateListener(OnStateListener listener) {
        mStateListener = listener;
        return this;
    }

    /**
     * Check if action menu is opened or not
     *
     * @return true if open
     */
    public boolean isOpening() {
        return mOpening;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Create new menu item
     *
     * @param key           unique key
     * @param resourceLabel resource label
     * @param resourceIcon  resource icon for button
     * @return menu item
     */
    public MenuItem newMenuItem(String key, int resourceLabel, int resourceIcon) {
        MenuItem item = new MenuItem(getContext());
        item.key = key;
        item._label.setText(resourceLabel);
        item._fbButton.setImageResource(resourceIcon);
        addItem(item);
        return item;
    }

    /**
     * Open action menu.
     *
     * @param animate set true for using animation
     */
    public void open(boolean animate) {
        open(animate, DEFAULT_ANIMATE_INTERVAL);
    }

    /**
     * Open action menu.
     *
     * @param animate  set true to animate opening action menu
     * @param duration the interval of showing each item sequentially
     */
    public void open(boolean animate, int duration) {
        if (isOpening() || mClosingAnim.isRunning() || mOpeningAnim.isRunning()) return;
        _fbMenuBtn.startAnimation(mfbMenuBtnAnim); // animate the scale of menu button
        if (mStateListener != null) mStateListener.onMenuOpening(this);
        if (animate) {
            post(mOpeningAnim.start(duration));
        } else {
            for (MenuItem item : mMenuItems) {
                item.setVisibility(VISIBLE);
            }
            _fbMenuBtn.setImageResource(R.drawable.ic_x_black);
            if (mStateListener != null) mStateListener.onMenuOpened(this);
        }
        mOpening = true;
    }

    /**
     * Close action menu
     *
     * @param animate set true to animate closing action menu
     */
    public void close(boolean animate) {
        close(animate, DEFAULT_ANIMATE_INTERVAL);
    }

    /**
     * Close action menu
     *
     * @param animate  set true to animate closing action menu
     * @param duration the interval of showing each item sequentially
     */
    public void close(boolean animate, int duration) {
        if (!isOpening() || mClosingAnim.isRunning() || mOpeningAnim.isRunning()) return;
        if (mStateListener != null) mStateListener.onMenuClosing(this);
        if (animate) {
            post(mClosingAnim.start(duration));
        } else {
            for (MenuItem item : mMenuItems) {
                item.setVisibility(GONE);
            }
            mfbMenuBtnAnim.cancel();
            _fbMenuBtn.setImageResource(R.drawable.ic_circle_menu);
            if (mStateListener != null) mStateListener.onMenuClosed(this);
        }
        mOpening = false;
    }

    /**
     * Toggle action menu
     *
     * @param animate set true to animate closing action menu
     */
    public void toggle(boolean animate) {
        toggle(animate, DEFAULT_ANIMATE_INTERVAL);
    }

    /**
     * Toggle parent
     *
     * @param animate  set true to animate closing action menu
     * @param duration the interval of showing each item sequentially
     */
    public void toggle(boolean animate, int duration) {
        if (isOpening()) {
            close(animate, duration);
        } else {
            open(animate, duration);
        }
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Menu state listener
     */
    public interface OnStateListener {

        /**
         * Trigger before opening action parent
         *
         * @param menu parent action
         */
        void onMenuOpening(CWFloatingActionMenu menu);

        /**
         * Trigger after opening action parent completely
         *
         * @param menu action parent
         */
        void onMenuOpened(CWFloatingActionMenu menu);

        /**
         * Trigger before closing action parent
         *
         * @param menu action parent
         */
        void onMenuClosing(CWFloatingActionMenu menu);

        /**
         * Trigger after closing action parent completely
         *
         * @param menu action parent
         */
        void onMenuClosed(CWFloatingActionMenu menu);
    }

    /**
     * Menu item
     */
    public class MenuItem extends LinearLayout {

        private CWFloatingActionMenu _parent;
        private TextView _label;
        private FloatingActionButton _fbButton;
        private CWTag tag = new CWTag();
        private String key;

        public MenuItem(Context context) {
            this(context, null, 0);
        }

        public MenuItem(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public MenuItem(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                inflater.inflate(R.layout.floating_action_menu_item, this, true);

                // bind views
                this._label = findViewById(R.id.label);
                this._fbButton = findViewById(R.id.button);
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parent().close(true);
                    }
                });
            }
        }

        /**
         * @return tagging model
         */
        public CWTag tag() {
            return this.tag;
        }

        /**
         * @return label view
         */
        public TextView label() {
            return this._label;
        }

        /**
         * @return item button
         */
        public FloatingActionButton button() {
            return this._fbButton;
        }

        /**
         * @return key
         */
        public String key() {
            return this.key;
        }

        /**
         * @return parent action parent
         */
        public CWFloatingActionMenu parent() {
            return this._parent;
        }

        /**
         * Set parent parent
         *
         * @param menu parent
         */
        private void parent(CWFloatingActionMenu menu) {
            this._parent = menu;
        }
    }

    /**
     * Animation for opening parent
     */
    private final class OpeningAnimation implements Runnable {

        private CWFloatingActionMenu _menu;
        private int duration;
        private ListIterator<MenuItem> loopIt;
        private boolean running = false;

        OpeningAnimation(CWFloatingActionMenu menu) {
            this._menu = menu;
        }

        @Override
        public void run() {
            if (!this.running) return;

            if (this.loopIt.hasPrevious()) {
                // show menu item sequentially
                MenuItem item = this.loopIt.previous();
                item.setVisibility(VISIBLE);
                item.postDelayed(this, this.duration);

            } else {
                // done opening parent
                this.running = false;
                _fbMenuBtn.setImageResource(R.drawable.ic_x_black);
                if (this._menu.getStateListener() != null) {
                    this._menu.getStateListener().onMenuOpened(this._menu);
                }
            }
        }

        private boolean isRunning() {
            return this.running;
        }

        private OpeningAnimation start(int duration) {
            this.loopIt = this._menu.mMenuItems.listIterator(mMenuItems.size());
            this.duration = duration;
            this.running = true;
            return this;
        }
    }

    /**
     * Animation for closing parent
     */
    private class ClosingAnimation implements Runnable {

        private CWFloatingActionMenu _menu;
        private int duration;
        private Iterator<MenuItem> loopIt;
        private boolean running = false;

        ClosingAnimation(CWFloatingActionMenu menu) {
            this._menu = menu;
        }

        @Override
        public void run() {
            if (!this.running) return; // stop animation

            if (this.loopIt.hasNext()) {
                // hide menu item sequentially
                MenuItem item = this.loopIt.next();
                item.setVisibility(GONE);
                item.postDelayed(this, this.duration);

            } else {
                // complete closing
                running = false;
                Animation anim = this._menu.button().getAnimation();
                if (anim != null) anim.cancel();
                _fbMenuBtn.setImageResource(R.drawable.ic_circle_menu);
                if (this._menu.getStateListener() != null) {
                    this._menu.getStateListener().onMenuClosed(this._menu);
                }
            }
        }

        private boolean isRunning() {
            return this.running;
        }

        private ClosingAnimation start(int duration) {
            this.loopIt = this._menu.mMenuItems.iterator();
            this.duration = duration;
            this.running = true;
            return this;
        }
    }
}
