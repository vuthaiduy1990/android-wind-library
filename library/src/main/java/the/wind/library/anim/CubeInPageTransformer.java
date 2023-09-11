package the.wind.library.anim;

import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

/**
 * Thank to Rahul Huria at <a href="https://www.loginworks.com/">...</a>
 */
public class CubeInPageTransformer implements ViewPager2.PageTransformer {

    @Override
    public void transformPage(View page, float position) {
        // Rotate the fragment on the left or right edge
        page.setPivotX(position > 0 ? 0 : page.getWidth());
        page.setPivotY(0);
        page.setRotationY(-90f * position);
    }
}
