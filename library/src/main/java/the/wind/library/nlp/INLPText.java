package the.wind.library.nlp;

/**
 * Text type interface
 */
public interface INLPText {

    /**
     * Convert object to text value
     */
    String toTextValue();

    /**
     * Get NLP text which has been processed by NLP engine
     *
     * @return NLP text
     */
    String nlpText();

    /**
     * Set text which has been processed by NLP engine
     *
     * @param nlpText text
     */
    void nlpText(String nlpText);
}