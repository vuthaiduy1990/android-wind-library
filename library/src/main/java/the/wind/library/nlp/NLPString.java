package the.wind.library.nlp;

import android.content.Context;

import java.util.Date;

import androidx.annotation.Nullable;

/**
 * A wrapper of string value
 */
public class NLPString implements INLPText {

    private CharSequence value;
    private String hash;

    public NLPString(CharSequence value) {
        this.value = value;
        this.hash = Double.toString(Math.random()) + new Date().getTime();
    }

    @Override
    public String nlpTextId(@Nullable Context context) {
        return hash;
    }

    @Override
    public String nlpRawText(@Nullable Context context) {
        return this.value.toString();
    }
}
