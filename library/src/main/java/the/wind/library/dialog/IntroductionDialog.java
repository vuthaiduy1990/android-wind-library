package the.wind.library.dialog;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import the.wind.library.R;
import the.wind.library.view.Button;
import the.wind.library.view.ImageSlider;

public class IntroductionDialog extends WindDialog {

    private final ImageSlider _imageSlider;

    /**
     * Constructor
     *
     * @param context activity context
     */
    public IntroductionDialog(@NonNull Context context) {
        super(context, LayoutType.FUBUKI);
        setContentView(R.layout.wl_introduction_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setTitleVisible(false);

        // bind views
        View contentView = contentView();
        _imageSlider = contentView.findViewById(R.id._imageSlider);

        // bind button
        addButton(Button.Type.GRAY, context.getString(R.string.wl_close), null).setOnClickListener(v -> dismiss());
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return image slider
     */
    public ImageSlider getImageSlider() {
        return _imageSlider;
    }

    /**
     * Set dataset
     *
     * @param dataset dataset
     */
    public void setDataset(ImageSlider.Data[] dataset) {
        _imageSlider.setDataset(dataset);
    }

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

}
