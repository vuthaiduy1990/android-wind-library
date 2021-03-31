package the.wind.library.anim;

import android.widget.TextView;

import java.util.Arrays;
import java.util.Iterator;

import the.wind.library.utils.CWStringUtils;

/**
 * Simulator typing text.
 * Show text word by word progressively
 * <pre>
 *     For example: run("The color of the wind")
 *     -> The
 *     -> The color
 *     -> The color of
 *     -> The color of the
 *     -> The color of the wind
 * </pre>
 */
public class TextTypingFakulator implements Runnable {

    // Text view
    private final TextView _textView;

    // text will be split into list of words
    private String text;
    private Iterator<String> words;
    private String regex;
    // interval between showing next words
    private long interval;

    // running flag
    private boolean running = false;


    // Listener
    private OnTypingListener typingListener;

    /**
     * Constructor
     *
     * @param textView text view
     */
    public TextTypingFakulator(TextView textView) {
        _textView = textView;
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public void run() {
        if (running && words.hasNext()) {
            _textView.append(words.next()); // add word gradually
            if (typingListener != null) typingListener.onTyping(this, _textView);
            _textView.postDelayed(this, interval);
        } else {
            running = false;
            if (typingListener != null) typingListener.onEnd(this, _textView);
        }
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * Set on typing listener
     *
     * @param listener on typing listener
     */
    public void setOnTypingListener(OnTypingListener listener) {
        typingListener = listener;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Configure the fakulator
     *
     * @param text     text
     * @param regex    split regex
     * @param interval interval time to append next symbol to text. Unit is millisecond
     */
    public TextTypingFakulator config(String text, String regex, long interval) {
        this.text = text;
        this.regex = regex;
        this.interval = interval;
        return this;
    }

    /**
     * Start fakulating
     */
    public void start() {
        startDelay(0);
    }

    /**
     * Start with delayed time
     *
     * @param delay delayed time before starting. Unit is millisecond
     */
    public void startDelay(long delay) {
        if (regex != null) {
            words = Arrays.asList(text.split(regex)).iterator();
        } else {
            words = CWStringUtils.text2words(text).iterator();
        }
        _textView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (typingListener != null) typingListener.onStart(TextTypingFakulator.this, _textView);
                running = true;
                TextTypingFakulator.this.run();
            }
        }, delay);
    }

    /**
     * Stop fakulating
     */
    public void stop() {
        running = false;
    }

    /* ---------------------- INNER CLASS -------------------- */

    public interface OnTypingListener {

        /**
         * Trigger when fakulator starts fakulating
         *
         * @param fakulator fakulator
         * @param textView  text view
         */
        void onStart(TextTypingFakulator fakulator, TextView textView);

        /**
         * Trigger when new text is typing
         *
         * @param fakulator fakulator
         * @param textView  text view
         */
        void onTyping(TextTypingFakulator fakulator, TextView textView);

        /**
         * Trigger when fakulator finish fakulating
         *
         * @param fakulator fakulator
         * @param textView  text view
         */
        void onEnd(TextTypingFakulator fakulator, TextView textView);
    }
}
