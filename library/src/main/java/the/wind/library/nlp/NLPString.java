package the.wind.library.nlp;

/**
 * A wrapper of string value
 */
public class NLPString implements INLPText {

    private CharSequence value;
    private String nlpText;

    public NLPString(CharSequence value) {
        this.value = value;
    }

    @Override
    public String toTextValue() {
        return this.value.toString();
    }

    @Override
    public String nlpText() {
        return nlpText;
    }

    @Override
    public void nlpText(String nlpText) {
        this.nlpText = nlpText;
    }
}
