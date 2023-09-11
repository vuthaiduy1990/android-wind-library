package the.wind.library.anim;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

/**
 * Thank to Rahul Huria at <a href="https://www.loginworks.com/">...</a>
 */
public class ZoomInTransformer implements ViewPager2.PageTransformer {
    public static final float MAX_ROTATION = 90.0f;

    @Override
    public void transformPage(View page, float pos) {
        final float scale = pos < 0 ? pos + 1f : Math.abs(1f - pos);
        page.setScaleX(scale);
        page.setScaleY(scale);
        page.setPivotX(page.getWidth() * 0.5f);
        page.setPivotY(page.getHeight() * 0.5f);
        page.setAlpha(pos < -1f || pos > 1f ? 0f : 1f - (scale - 1f));
    }
}
