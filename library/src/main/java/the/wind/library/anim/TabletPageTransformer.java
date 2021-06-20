package the.wind.library.anim;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * Thank to Rahul Huria at https://www.loginworks.com/
 */
public class TabletPageTransformer implements ViewPager.PageTransformer {

    private static final Matrix MATRIX_OFFSET = new Matrix();
    private static final Camera CAMERA_OFFSET = new Camera();
    private static final float[] TEMP_FLOAT_OFFSET = new float[2];

    @Override
    public void transformPage(View page, float pos) {
        final float rotation = (pos < 0 ? 30f : -30f) * Math.abs(pos);

        page.setTranslationX(getOffsetX(rotation, page.getWidth(), page.getHeight()));
        page.setPivotX(page.getWidth() * 0.5f);
        page.setPivotY(0);
        page.setRotationY(rotation);
    }

    private float getOffsetX(float rotation, int width, int height) {
        MATRIX_OFFSET.reset();
        CAMERA_OFFSET.save();
        CAMERA_OFFSET.rotateY(Math.abs(rotation));
        CAMERA_OFFSET.getMatrix(MATRIX_OFFSET);
        CAMERA_OFFSET.restore();

        MATRIX_OFFSET.preTranslate(-width * 0.5f, -height * 0.5f);
        MATRIX_OFFSET.postTranslate(width * 0.5f, height * 0.5f);
        TEMP_FLOAT_OFFSET[0] = width;
        TEMP_FLOAT_OFFSET[1] = height;
        MATRIX_OFFSET.mapPoints(TEMP_FLOAT_OFFSET);
        return (width - TEMP_FLOAT_OFFSET[0]) * (rotation > 0.0f ? 1.0f : -1.0f);

    }
}