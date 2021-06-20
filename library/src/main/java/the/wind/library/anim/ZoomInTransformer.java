package the.wind.library.anim;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * Thank to Rahul Huria at https://www.loginworks.com/
 */
public class ZoomInTransformer implements ViewPager.PageTransformer {
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
