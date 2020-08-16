package the.wind.library;

import androidx.annotation.NonNull;

/**
 * Example.
 * <pre>
 *     public void handleSomething(CWCallback<String> callback) {
 *          //  implement the function
 *          for ... loop {
 *              callback.onSuccess(xxx);
 *          }
 *          callback.onEnd();
 *     }
 *
 *     // Call the function
 *     handleSomething(new CWCallback<String>(){
 *          @Override
 *          public String onSuccess(String result) {
 *              // handle something here
 *              return super.onSuccess(result);
 *          }
 *
 *          @Override
 *          public void onEnd() {
 *              super.onEnd();
 *          }
 *     });
 * </pre>
 *
 * @param <T> generic type
 */
public class CWCallback<T> {

    // tagged object
    private CWBundle mBundle = new CWBundle();

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /**
     * On begin of doing something
     */
    public void onBegin() {
    }

    /**
     * On end of doing something
     */
    public void onEnd() {
    }

    /**
     * On returning successfully
     *
     * @return result
     */
    public T onSuccess(T result) {
        onResult(result, null);
        return result;
    }

    /**
     * On returning fail
     *
     * @param t throwable
     * @return true/false. You can use this returned value to decide whether stop process or not
     */
    public boolean onFail(Throwable t) {
        onResult(null, t);
        return true;
    }

    /**
     * On result
     *
     * @param result result if success
     * @param t      error if failed
     */
    public void onResult(T result, Throwable t) {
    }

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return bundle
     */
    @NonNull
    public CWBundle bundle() {
        return mBundle;
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- OVERRIDE ----------------------- */

}