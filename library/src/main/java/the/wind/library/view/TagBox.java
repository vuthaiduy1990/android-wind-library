package the.wind.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import the.wind.library.CWBundle;
import the.wind.library.R;
import the.wind.library.WindColor;
import the.wind.library.utils.CWColorUtils;

/**
 * Define tagging box
 */
public class TagBox extends FlexboxLayout {

    private final ViewGroup _boxLayout;
    private final Map<String, View> viewMap = new HashMap<>();
    // bundle data
    private final CWBundle bundle = new CWBundle();
    // Views
    private final LayoutInflater inflater;
    // styling
    private float textSize;
    private float textMaxWidth;
    @ColorInt
    private int textColor;
    @DrawableRes
    private int removeIconRes;
    private float iconSize;
    // List of tag values
    @NonNull
    private Set<String> tags = new HashSet<>();
    // List of colors
    @Nullable
    private List<String> colors;
    private Iterator<String> colorIt;
    // Listener
    private OnItemClickListener itemClickListener;
    private OnItemRemoveListener itemRemoveListener;

    /**
     * Constructor
     *
     * @param context application context
     */
    public TagBox(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context application context
     * @param attrs   collection of attributes
     */
    public TagBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context      application context
     * @param attrs        collection of attributes
     * @param defStyleAttr style attribute
     */
    public TagBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.wl_tag_box, this);

        // bind views
        _boxLayout = findViewById(R.id._boxLayout);

        // bind styling attribute
        TypedArray typeArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.TagBox,
                defStyleAttr, 0);

        try {
            // bind text attributes
            textSize = typeArray.getDimension(
                    R.styleable.TagBox_textSize,
                    getResources().getDimension(R.dimen.wl_text_small));
            textMaxWidth = typeArray.getDimension(
                    R.styleable.TagBox_textMaxWidth,
                    getResources().getDimension(R.dimen.wl_tag_box_item_max_with));
            textColor = typeArray.getColor(
                    R.styleable.TagBox_textColor,
                    ContextCompat.getColor(context, R.color.wl_text));

            // bind icon attributes
            iconSize = typeArray.getDimension(
                    R.styleable.TagBox_iconSize,
                    getResources().getDimension(R.dimen.wl_icon_tiny));
            removeIconRes = typeArray.getResourceId(R.styleable.TagBox_removeIcon, R.drawable.wl_ic_delete_across);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            typeArray.recycle();
        }
    }

    /* ---------------------- OVERRIDE ----------------------- */

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
     * @return all tags value
     */
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Set tags
     *
     * @param tags tags
     * @return tag box
     */
    public TagBox setTags(Set<String> tags) {
        clear();
        if (tags != null) {
            // make a clone
            this.tags = new HashSet<>(tags);
            for (String tag : tags) {
                addTagItemView(tag);
            }
        }
        return this;
    }

    /**
     * Add new tag
     *
     * @param tag new tag
     */
    public void add(String tag) {
        if (tag != null && !tag.trim().isEmpty() && !tags.contains(tag)) {
            this.tags.add(tag);
            addTagItemView(tag);
        }
    }

    /**
     * Remove a tag
     *
     * @param tag tag value
     */
    public void remove(String tag) {
        tags.remove(tag);
        _boxLayout.removeView(viewMap.get(tag));
        viewMap.remove(tag);
    }

    /**
     * Clear all tags
     */
    public void clear() {
        tags.clear();
        _boxLayout.removeAllViews();
        viewMap.clear();
    }

    /**
     * Set tag colors
     *
     * @param colors list of colors
     * @return tag box
     */
    public TagBox setColors(List<String> colors) {
        this.colors = colors;
        return this;
    }

    /**
     * Set tag colors
     *
     * @param colors list of colors
     * @return tag box
     */
    public TagBox setColors(String... colors) {
        this.colors = Arrays.asList(colors);
        return this;
    }

    /**
     * Get color for tag
     *
     * @return random/predefined color for tag
     */
    private WindColor getColor() {
        String color;
        if (colors != null && !colors.isEmpty()) {
            // retrieve from  pre-defined colors
            if (colorIt == null || !colorIt.hasNext()) {
                colorIt = this.colors.iterator();
            }
            color = colorIt.next();
        } else {
            // retrieve from POKE colors
            color = CWColorUtils.randomPokeColor();
        }
        return WindColor.fromHex(color);
    }

    /**
     * Set text size
     *
     * @param restId text size resource id
     * @return text box
     */
    public TagBox setTextSize(@DimenRes int restId) {
        this.textSize = getResources().getDimension(restId);
        return this;
    }

    /**
     * Set text max width
     *
     * @param resId dimension resource id
     * @return tag box
     */
    public TagBox setTextMaxWidth(@DimenRes int resId) {
        textMaxWidth = getResources().getDimension(resId);
        return this;
    }

    /**
     * Set text color
     *
     * @param restId color resource id
     * @return tag box
     */
    public TagBox setTextColor(@ColorRes int restId) {
        this.textColor = ContextCompat.getColor(getContext(), restId);
        return this;
    }

    /**
     * Set icon size
     *
     * @param restId icon size resource id
     * @return tag box
     */
    public TagBox setIconSize(@DimenRes int restId) {
        this.iconSize = getResources().getDimension(restId);
        return this;
    }

    /**
     * Set remove icon
     *
     * @param restId icon resource id
     * @return tag box
     */
    public TagBox setRemoveIconRes(@DrawableRes int restId) {
        removeIconRes = restId;
        return this;
    }

    /**
     * Set item tag view click listener
     *
     * @param listener click listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    /**
     * Set item tag remove listener
     *
     * @param listener remove listener
     */
    public void setOnItemRemoveListener(OnItemRemoveListener listener) {
        this.itemRemoveListener = listener;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Add tag view
     *
     * @param tag tag value
     * @return tag view
     */
    private View addTagItemView(final String tag) {
        final View itemView = inflater.inflate(R.layout.wl_tag_box_item, _boxLayout, false);
        viewMap.put(tag, itemView);

        // bind text view
        TextView _textView = itemView.findViewById(R.id._textView);
        _textView.setText(tag);
        _textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        _textView.setTextColor(textColor);
        _textView.setMaxWidth((int) textMaxWidth);

        // bind icon view
        ImageView _removeIcon = itemView.findViewById(R.id._removeIcon);
        _removeIcon.setImageResource(removeIconRes);
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams((int) iconSize, (int) iconSize);
        iconLp.setMarginStart((int) getResources().getDimension(R.dimen.wl_spacing_level_1));
        _removeIcon.setLayoutParams(iconLp);
        _removeIcon.setTag(itemView);

        // bind color
        WindColor bgColor = getColor();
        GradientDrawable drawable = (GradientDrawable) itemView.getBackground().getCurrent();
        drawable.mutate();
        drawable.setColor(bgColor.value());

        // bind view event listener
        _removeIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemRemoveListener != null) {
                    if (itemRemoveListener.onRemove(itemView, tag)) {
                        remove(tag);
                    }
                } else {
                    remove(tag);
                }
            }
        });
        itemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(itemView, tag);
                }
            }
        });

        _boxLayout.addView(itemView);
        return itemView;
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * On tag item remove listener
     */
    public interface OnItemRemoveListener {
        /**
         * trigger when user delete an item
         *
         * @param view tag view
         * @param tag  tag value
         * @return true if delete successfully, else return false
         */
        boolean onRemove(View view, String tag);
    }

    /**
     * On tag item click listener
     */
    public interface OnItemClickListener {
        /**
         * Trigger when user click on the tag item view
         *
         * @param view tag view
         * @param tag  tag value
         */
        void onClick(View view, String tag);
    }

}
