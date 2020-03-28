package the.wind.library.async;

/**
 * <p>Sequence Diagram</p>
 * P: promise which manage the real operation
 * C: callback listener
 * HP: holder promise which hold callback listener and next holder promise
 * <pre>
 *     [P1] include [C1] + [HP2]
 *     P1.resolve() -> C1.callback() return P2
 *
 *     [P2] include [HP2]
 *     [HP2] include [C2] + [HP3]
 *     P2.resolve -> HP2.resolve() -> C2.callback return P3
 *
 *     [P3] include [HP3]
 *     [HP2] include [C2] + [HP3]
 *     P3.resolve -> HP3.resolve() -> C2.callback return P4
 * </pre>
 *
 * <p>Examples</p>
 * <pre>
 *      private class PrintTextPromise implements Promise.IPromise<String> {
 *         private Promise<String> mPromise = new Promise<>();
 *
 *         @Override
 *         public Promise<String> promise() {
 *             return mPromise;
 *         }
 *
 *         public PrintTextPromise print(final String text) {
 *             final int delay = CWMathUtils.random(100, 200);
 *             mTimer.schedule(new TimerTask() {
 *                 @Override
 *                 public void run() {
 *                     if (text == null || text.trim().isEmpty()) {
 *                         mPromise.resolve(new NullPointerException());
 *                     } else {
 *                         mPromise.resolve(text);
 *                     }
 *                 }
 *             }, delay);
 *             return this;
 *         }
 *     }
 *
 *     // Success callback example
 *     Promise.wrap(new PrintTextPromise().print("color")).then(new Promise.OnSuccessListener<String, String>() {
 *          @Override
 *          public Promise.IPromise<String> onSuccess(String data) {
 *              Assert.assertEquals("color", data);
 *              return new PrintTextPromise().print("wind");
 *          }
 *
 *     }).then(new Promise.OnSuccessListener<String, Integer>() {
 *          @Override
 *          public Promise.IPromise<Integer> onSuccess(String data) {
 *              Assert.assertEquals("wind", data);
 *              return new PrintNumberPromise().print(69);
 *          }
 *
 *     }).finish(new Promise.OnFinishListener() {
 *          @Override
 *          public void onFinish(Throwable throwable, Object data) {
 *              Assert.assertEquals("wind", data);
 *              System.out.println("Finally!");
 *         }
 *     });
 *
 *     // exception callback listener
 *     Promise.wrap(new PrintTextPromise().print("color")).then(new Promise.OnSuccessListener<String, String>() {
 *          @Override
 *          public Promise.IPromise<String> onSuccess(String data) {
 *              Assert.assertEquals("color", data);
 *                 // this will trigger an exception
 *                 // --->> other next promise will not be resolved
 *                 return new PrintTextPromise().print(null);
 *             }
 *
 *     }).then(new Promise.OnSuccessListener<String, Integer>() { // -->> this promise will be skipped
 *          @Override
 *          public Promise.IPromise<Integer> onSuccess(String data) {
 *              Assert.fail();
 *              return new PrintNumberPromise().print(69);
 *          }
 *
 *     }).then(new Promise.OnSuccessListener<Integer, Integer>() { // -->> this promise will be skipped
 *          @Override
 *          public Promise.IPromise<Integer> onSuccess(Integer data) {
 *              return new PrintNumberPromise().print(96);
 *          }
 *
 *     }).exception(new Promise.OnExceptionListener() {
 *          @Override
 *          public void onException(Throwable throwable) {
 *              System.out.println("Exception!");
 *              Assert.assertTrue(throwable instanceof NullPointerException);
 *          }
 *
 *     }).finish(new Promise.OnFinishListener() {
 *          @Override
 *          public void onFinish(Throwable throwable, Object data) {
 *              Assert.assertTrue(throwable instanceof NullPointerException);
 *              Assert.assertNull(data);
 *              System.out.println("Finally!");
 *             }
 *     });
 *
 *
 * </pre>
 *
 * @param <Model> data model
 */
public final class Promise<Model> {

    private Promise<Model> mCurrentHolderPromise;
    private Promise mNextHolderPromise; // placeholder promise which hold listener for real promise

    // Listeners
    private OnSuccessListener mSuccessListener;
    private OnExceptionListener mExceptionListener;
    private OnFinishListener mFinishListener;

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /**
     * Wrap a promise instance
     *
     * @param promise handler instance which implement Promise
     * @param <Model> data model
     * @return a promise
     */
    public static <Model> Promise<Model> wrap(IPromise<Model> promise) {
        return promise.promise();
    }

    /* ---------------------- ABSTRACT -----------------------*/

    /* ---------------------- GET-SET ------------------------ */

    /* ---------------------- METHOD ------------------------- */

    /**
     * Add success listener
     *
     * @param listener        success listener
     * @param <NextDataModel> data model returned by next promise
     * @return next promise
     */
    @SuppressWarnings("unchecked")
    public <NextDataModel> Promise<NextDataModel> then(OnSuccessListener<Model, NextDataModel> listener) {
        mSuccessListener = listener;
        mNextHolderPromise = new Promise<NextDataModel>();
        return mNextHolderPromise;
    }

    /**
     * Add exception listener
     *
     * @param listener exception listener
     * @return next promise
     */
    public Promise<?> exception(OnExceptionListener listener) {
        mExceptionListener = listener;
        mNextHolderPromise = new Promise<>();
        return mNextHolderPromise;
    }

    /**
     * Add finish listener.
     * This listener should be added at the end of promise chain
     *
     * @param listener finish listener
     */
    public void finish(OnFinishListener listener) {
        mFinishListener = listener;
    }

    /**
     * Resolve success data
     *
     * @param data data
     */
    @SuppressWarnings("unchecked")
    public void resolve(Model data) {
        // Reach the end of promise chain  -> trigger finish listener
        if (mFinishListener != null) {
            mFinishListener.onFinish(null, data);
            return;
        }

        if (mCurrentHolderPromise == null) /*no holder promise*/ {
            // execute success callback and retrieve the real next promise
            if (mSuccessListener != null) {
                IPromise realNext = mSuccessListener.onSuccess(data);
                if (realNext != null) {
                    // bind holder promise to real promise
                    // --->> so that when real promise execute operation,
                    // --->> it will trigger the callback listener hold by holder promise
                    Promise.wrap(realNext).bind(mNextHolderPromise);
                } else {
                    // no next promise returned
                    // --->> this is the last one (should be Finish promise)
                    // --->> resolve Finish promise which then trigger OnFinishListener callback
                    mNextHolderPromise.resolve(data);
                }
            }

        } else {
            // Resolve current holder promise which then trigger a callback listener
            mCurrentHolderPromise.resolve(data);
        }
    }

    /**
     * Resolve exception
     *
     * @param throwable error or exception
     */
    @SuppressWarnings("unchecked")
    public void resolve(Throwable throwable) {
        // Reach the end of promise chain  -> trigger finish listener
        if (mFinishListener != null) {
            mFinishListener.onFinish(throwable, null);
            return;
        }

        // Reach any exception listener in promise chain
        // ---> trigger exception event
        if (mExceptionListener != null) {
            mExceptionListener.onException(throwable);
        }

        // Resolve the next promise in promise chain
        if (mCurrentHolderPromise != null) {
            mCurrentHolderPromise.resolve(throwable);

        } else if (mNextHolderPromise != null) {
            mNextHolderPromise.resolve(throwable);
        }
    }

    /**
     * Bind holder promise
     *
     * @param holder holder promise
     */
    private void bind(Promise<Model> holder) {
        mCurrentHolderPromise = holder;
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Promise interface
     *
     * @param <T> generic data model
     */
    public interface IPromise<T> {
        /**
         * @return a Promise
         */
        Promise<T> promise();
    }

    /**
     * On success listener
     *
     * @param <Model>         data model returned by current promise
     * @param <NextDataModel> data model returned by next promise
     */
    public interface OnSuccessListener<Model, NextDataModel> {
        /**
         * Trigger when resolving data result in success
         *
         * @param data data
         * @return next promise
         */
        IPromise<NextDataModel> onSuccess(Model data);
    }

    /**
     * On exception/error listener
     */
    public interface OnExceptionListener {
        /**
         * Trigger when resolving data result in fail
         *
         * @param throwable error or exception
         */
        void onException(Throwable throwable);
    }

    /**
     * On finish listener
     */
    public interface OnFinishListener {
        /**
         * Trigger when there is no promise to execute next
         *
         * @param throwable error or exception
         * @param data      data returned by the last promise
         */
        void onFinish(Throwable throwable, Object data);
    }

}


