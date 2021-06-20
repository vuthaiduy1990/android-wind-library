package the.wind.library.anim;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * Thank to Rahul Huria at https://www.loginworks.com/
 */

public class FlipVerticalPageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float pos) {
        final float rotation = -180f * pos;

        page.setAlpha(rotation > 90f || rotation < -90f ? 0f : 1f);
        page.setPivotX(page.getWidth() * 0.5f);
        page.setPivotY(page.getHeight() * 0.5f);
        page.setRotationX(rotation);
    }
}