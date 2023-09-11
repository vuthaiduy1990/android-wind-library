package the.wind.library.anim;

import androidx.viewpager2.widget.ViewPager2;

public enum PageTransformerType {

    BACKGROUND_TO_FOREGROUND(new BackgroundToForegroundPageTransformer()),
    FOREGROUND_TO_BACKGROUND(new ForegroundToBackgroundPageTransformer()),
    CUBE_IN(new CubeInPageTransformer()),
    CUBE_OUT(new CubeOutPageTransformer()),
    DEPTH(new DepthPageTransformer()),
    FLIP_HORIZONTAL(new FlipHorizontalPageTransformer()),
    FLIP_VERTICAL(new FlipVerticalPageTransformer()),
    ROTATE_DOWN(new RotateDownPageTransformer()),
    ROTATE_UP(new RotateUpPageTransformer()),
    TABLET(new TabletPageTransformer()),
    ZOOM_IN(new ZoomInTransformer()),
    ZOOM_OUT(new ZoomOutPageTransformer());

    // transformer
    private final ViewPager2.PageTransformer transformer;

    /**
     * Constructor
     *
     * @param transformer transformer
     */
    PageTransformerType(ViewPager2.PageTransformer transformer) {
        this.transformer = transformer;
    }

    /**
     * @return transformer
     */
    public ViewPager2.PageTransformer getTransformer() {
        return transformer;
    }

}
