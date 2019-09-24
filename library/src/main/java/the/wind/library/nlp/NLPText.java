package the.wind.library.nlp;

/**
 * NLP Text. A wrapper of string type
 */
public final class NLPText {

    // mTarget
    // this object should be String type or class which implements {{ITextType}}
    private ITextType mTarget;

    // string value which is respective to mTarget
    private String mTextValue;

    /**
     * Construct a NLP text
     *
     * @param target mTarget object
     */
    public NLPText(ITextType target) {
        setTarget(target);
    }

    /**
     * Construct a NLP text
     *
     * @param target mTarget string
     */
    public NLPText(CharSequence target) {
        setTarget(new StringWrapper(target));
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- EVENT -------------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return target object which implement {{{@link ITextType}}}
     */
    public ITextType target() {
        return mTarget;
    }

    /**
     * Set target
     *
     * @param target target
     */
    private void setTarget(ITextType target) {
        mTarget = target;
        refresh();
    }

    /**
     * @return text value
     */
    public String getText() {
        return mTextValue;
    }

    /**
     * Set text value
     *
     * @param text text value
     */
    protected void setText(String text) {
        mTextValue = text;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Refresh NLP text.
     * This process will update the text value based on target changed
     */
    protected void refresh() {
        setText(mTarget.toTextValue());
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Text type interface
     */
    public interface ITextType {
        /**
         * Convert object to text value
         */
        String toTextValue();
    }

    /**
     * A wrapper of string value
     */
    private class StringWrapper implements ITextType {
        private CharSequence value;

        private StringWrapper(CharSequence value) {
            this.value = value;
        }

        @Override
        public String toTextValue() {
            return this.value.toString();
        }
    }
}
