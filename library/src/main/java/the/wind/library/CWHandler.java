package the.wind.library;

/**
 * Example: run a handler repeatedly and you can pause/resume/destroy the executing handler
 * <pre>
 *     // Control the handler
 *     Iterator<GMHandler<?>> it = mRepeatHandlers.iterator();
 *     while ((it.hasNext())) {
 *          GMHandler<?> handler = it.next();
 *          switch (handler.state()) {
 *              case RUNNING:
 *                  // execute the handler
 *                  handler.onHandle(data);
 *                  break;
 *              case PAUSED:
 *                  // stop executing the handler
 *                  break;
 *              case DESTROYED:
 *                  it.remove();
 *                  break;
 *          }
 *      }
 *
 *      // execute something with the handler
 *      sendLove(new GMHandler<?>() {
 *          @Override
 *          public void onHandle(Object... params) {
 *              // implement some fuck code here
 *              ......
 *
 *              // decide the state of handler
 *              if (wait for love) {
 *                  pause();
 *              } else if (love is here) {
 *                  resume();
 *              } else if (love end) {
 *                  destroy()
 *              }
 *          }
 *      }
 *
 * </pre>
 *
 * @param <T> generic type
 */
public abstract class CWHandler<T> {

    private CWAttach mTag = new CWAttach();
    private State mState = State.RUNNING;

    // state
    public enum State {
        RUNNING,
        PAUSED,
        DESTROYED
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- OVERRIDE ----------------------- */

    /**
     * On before handling
     *
     * @param params arguments
     */
    public void onBefore(T... params) {
    }

    /**
     * Override this function to handle something here
     *
     * @param params arguments
     */
    public abstract void onHandle(T... params);

    /**
     * On after handling
     *
     * @param params arguments
     */
    public void onAfter(T... params) {
    }

    /* ---------------------- EVENT -------------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return attach
     */
    public CWAttach tag() {
        return mTag;
    }

    /**
     * Get current state of handler
     *
     * @return handler state
     */
    public State state() {
        return mState;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Resume the processing
     */
    public void resume() {
        mState = State.RUNNING;
    }

    /**
     * Pause the processing
     */
    public void pause() {
        mState = State.PAUSED;
    }

    /**
     * Destroy the processing
     */
    public void destroy() {
        mState = State.DESTROYED;
    }

    /* ---------------------- INNER CLASS -------------------- */
}
