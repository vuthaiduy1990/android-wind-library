package the.wind.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import the.wind.library.CWBundle;
import the.wind.library.R;

public class ImageSlider extends LinearLayout {

    private static final int DEFAULT_OFFSCREEN = 2;
    private static final int DEFAULT_MAX_DOTS = 10;

    // Views
    private final LayoutInflater inflater;
    private final ViewPager _viewpager;
    private final SliderAdapter adapter;
    private final ViewGroup _dotListView;
    private View preDotView;
    private final Map<Integer, View> dotViewMap = new HashMap<>();

    // style
    private float titleSize;
    @ColorInt
    private int titleColor;
    private float textSize;
    @ColorInt
    private int textColor;

    // Listener
    private ViewPager.OnPageChangeListener pageChangeListener;

    // data
    private final CWBundle mBundle = new CWBundle();
    private int maxDots = DEFAULT_MAX_DOTS;

    /**
     * Constructor
     *
     * @param context application context
     */
    public ImageSlider(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context application context
     * @param attrs   collection of attributes
     */
    public ImageSlider(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context      application context
     * @param attrs        collection of attributes
     * @param defStyleAttr style attribute
     */
    public ImageSlider(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * Constructor
     *
     * @param context      application context
     * @param attrs        collection of attributes
     * @param defStyleAttr style attribute
     * @param defStyleRes  style resource
     */
    public ImageSlider(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.wl_slider, this);

        // bind view
        _viewpager = findViewById(R.id._viewpager);
        _dotListView = findViewById(R.id._dotListView);

        // bind attributes
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.ImageSlider,
                defStyleAttr, defStyleRes);
        int offscreen = DEFAULT_OFFSCREEN;
        try {
            offscreen = typeArray.getInteger(R.styleable.ImageSlider_offscreen, DEFAULT_OFFSCREEN);
            maxDots = typeArray.getInteger(R.styleable.ImageSlider_maxDots, DEFAULT_MAX_DOTS);
            titleSize = typeArray.getDimension(
                    R.styleable.ImageSlider_titleSize,
                    getResources().getDimension(R.dimen.wl_text_intro_title));
            titleColor = typeArray.getColor(
                    R.styleable.ImageSlider_titleColor,
                    ContextCompat.getColor(context, R.color.wl_text));
            textSize = typeArray.getDimension(
                    R.styleable.ImageSlider_textSize,
                    getResources().getDimension(R.dimen.wl_text_small));
            textColor = typeArray.getColor(
                    R.styleable.ImageSlider_textColor,
                    ContextCompat.getColor(context, R.color.wl_text));

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            typeArray.recycle();
        }

        // Set adapter
        adapter = new SliderAdapter(context);
        adapter.titleColor = titleColor;
        adapter.titleSize = titleSize;
        adapter.textColor = textColor;
        adapter.textSize = textSize;
        _viewpager.setOffscreenPageLimit(offscreen);
        _viewpager.setAdapter(adapter);
        _viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do nothing
                if (pageChangeListener != null) {
                    pageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                ImageSlider.this.onPageSelected(position);
                if (pageChangeListener != null) {
                    pageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (pageChangeListener != null) {
                    pageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return bundle data
     */
    public CWBundle bundle() {
        return mBundle;
    }

    /**
     * Set dataset
     *
     * @param dataset dataset
     */
    public void setDataset(Data[] dataset) {
        adapter.dataset = dataset;
        adapter.notifyDataSetChanged();

        // Create dot respectively
        removeDots();
        createDots(Math.min(dataset.length, maxDots));
    }

    /**
     * Set offscreen
     *
     * @param offscreen offscreen
     */
    public void setOffScreen(int offscreen) {
        _viewpager.setOffscreenPageLimit(offscreen);
    }

    /**
     * Set max number of dots
     *
     * @param maxDots max number of dots
     */
    public void setMaxDots(int maxDots) {
        this.maxDots = maxDots;
        removeDots();
        int dsLength = adapter.dataset != null ? adapter.dataset.length : 0;
        if (dsLength > 0) {
            createDots(Math.min(dsLength, maxDots));
        }
    }

    /**
     * Set current item
     *
     * @param position specific position
     */
    public void setCurrentItem(int position) {
        _viewpager.setCurrentItem(position);
    }

    /**
     * Set on page change listener
     *
     * @param listener listener
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.pageChangeListener = listener;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Handle slider swipe
     *
     * @param position selected position
     */
    private void onPageSelected(int position) {
        // do nothing
        View dotView = dotViewMap.get(position);
        if (dotView != null) {
            if (preDotView != null) {
                preDotView.setBackgroundResource(R.drawable.wl_dot_view_inactive);
            }
            dotView.setBackgroundResource(R.drawable.wl_dot_view_active);
            preDotView = dotView;
        }
    }

    /**
     * Create dot
     *
     * @param num number of dot
     */
    private void createDots(int num) {
        for (int i = 0; i < num; i++) {
            View dotView = inflater.inflate(R.layout.wl_dot_view, _dotListView, false);
            if (i == 0) {
                preDotView = dotView;
                dotView.setBackgroundResource(R.drawable.wl_dot_view_active);
            } else {
                dotView.setBackgroundResource(R.drawable.wl_dot_view_inactive);
            }
            dotViewMap.put(i, dotView);
            _dotListView.addView(dotView);
        }
    }

    /**
     * Remove dots
     */
    private void removeDots() {
        _dotListView.removeAllViews();
        dotViewMap.clear();
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Slider data
     */
    public static class Data {

        @DrawableRes
        private final int imageResId;
        @StringRes
        private final int titleResId;
        @StringRes
        private final int textResId;

        /**
         * Constructor
         *
         * @param imageResId image source ID
         * @param textResId  text resource ID
         */
        public Data(@DrawableRes int imageResId, @StringRes int titleResId, @StringRes int textResId) {
            this.imageResId = imageResId;
            this.titleResId = titleResId;
            this.textResId = textResId;
        }
    }

    /**
     * Slider adapter
     */
    public static class SliderAdapter extends PagerAdapter {

        private final LayoutInflater inflater;


        // style
        private float titleSize;
        @ColorInt
        private int titleColor;
        private float textSize;
        @ColorInt
        private int textColor;

        private Data[] dataset = new Data[]{};

        /**
         * Slider adapter
         */
        private SliderAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return dataset.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            // bind view
            View view = inflater.inflate(R.layout.wl_slider_item_view, container, false);
            ImageView _imageView = view.findViewById(R.id._imageView);
            TextView _titleView = view.findViewById(R.id._titleView);
            TextView _textView = view.findViewById(R.id._textView);

            // bind style
            _titleView.setTextColor(titleColor);
            _titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
            _textView.setTextColor(textColor);
            _textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            // bind data
            Data data = dataset[position];
            _imageView.setImageResource(data.imageResId);
            if (data.titleResId > 0) {
                _titleView.setText(data.titleResId);
                _titleView.setVisibility(VISIBLE);
            } else {
                _titleView.setVisibility(GONE);
            }
            if (data.textResId > 0) {
                _textView.setText(data.textResId);
                _textView.setVisibility(VISIBLE);
            } else {
                _textView.setVisibility(GONE);
            }

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((ViewGroup) object);
        }
    }
}
