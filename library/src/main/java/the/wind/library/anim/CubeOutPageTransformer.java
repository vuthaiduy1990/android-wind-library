package the.wind.library.anim;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * Thank to Rahul Huria at https://www.loginworks.com/
 */
public class CubeOutPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View page, float pos) {
        page.setPivotX(pos < 0f ? page.getWidth() : 0f);
        page.setPivotY(page.getHeight() * 0.5f);
        page.setRotationY(90f * pos);
    }
}
