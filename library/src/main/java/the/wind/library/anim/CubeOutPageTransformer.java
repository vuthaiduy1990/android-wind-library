package the.wind.library.anim;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

/**
 * Thank to Rahul Huria at <a href="https://www.loginworks.com/">...</a>
 */
public class CubeOutPageTransformer implements ViewPager2.PageTransformer {

    @Override
    public void transformPage(View page, float pos) {
        page.setPivotX(pos < 0f ? page.getWidth() : 0f);
        page.setPivotY(page.getHeight() * 0.5f);
        page.setRotationY(90f * pos);
    }
}
