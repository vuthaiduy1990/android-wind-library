package the.wind.library.nlp;

import android.content.Context;

import androidx.annotation.Nullable;

/**
 * Text type interface
 */
public interface INLPText {

    /**
     * @param context application context.
     * @return unique id
     */
    String nlpTextId(@Nullable Context context);

    /**
     * @param context application context.
     *                It can be useful when you want to convert resource string
     * @return raw text value
     */
    String nlpRawText(@Nullable Context context);
}