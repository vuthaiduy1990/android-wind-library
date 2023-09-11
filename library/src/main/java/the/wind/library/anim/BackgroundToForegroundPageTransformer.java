package the.wind.library.anim;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

/**
 * Thank to Rahul Huria at <a href="https://www.loginworks.com/">...</a>
 */
public class BackgroundToForegroundPageTransformer implements ViewPager2.PageTransformer {
    @Override
    public void transformPage(View page, float pos) {
        final float height = page.getHeight();
        final float width = page.getWidth();
        final float scale = Math.min(pos < 0 ? 1f : Math.abs(1f - pos), 1f);

        page.setScaleX(scale);
        page.setScaleY(scale);
        page.setPivotX(width * 0.5f);
        page.setPivotY(height * 0.5f);
        page.setTranslationX(pos < 0 ? width * pos : -width * pos * 0.25f);
    }
}
