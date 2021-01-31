package the.wind.library;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    private final CWBundle bundle = new CWBundle();
    private State state = State.RUNNING;
    private final String taskName;


    /**
     * Default constructor
     */
    public CWHandler() {
        this(null);
    }

    /**
     * Constructor
     *
     * @param taskName task name
     */
    public CWHandler(String taskName) {
        this.taskName = taskName;
    }

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /**
     * On before handling
     *
     * @param params arguments
     */
    @SuppressWarnings("unchecked")
    public void onBefore(T... params) {
    }

    /**
     * Override this function to handle something here
     *
     * @param params arguments
     */
    @SuppressWarnings("unchecked")
    public abstract void onHandle(T... params);

    /**
     * On after handling
     *
     * @param params arguments
     */
    @SuppressWarnings("unchecked")
    public void onAfter(T... params) {
    }

    /* ---------------------- GET-SET ------------------------ */

    /**
     * @return task name
     */
    @Nullable
    public String getTaskName() {
        return taskName;
    }

    /**
     * @return bundle
     */
    @NonNull
    public CWBundle bundle() {
        return bundle;
    }

    /**
     * Get current state of handler
     *
     * @return handler state
     */
    @NonNull
    public State state() {
        return state;
    }

    /* ---------------------- METHOD ------------------------- */

    /**
     * Resume the processing
     */
    public void resume() {
        state = State.RUNNING;
    }

    /**
     * Pause the processing
     */
    public void pause() {
        state = State.PAUSED;
    }

    /**
     * Destroy the processing
     */
    public void destroy() {
        state = State.DESTROYED;
    }

    /* ---------------------- INNER CLASS -------------------- */

    // state
    public enum State {
        RUNNING,
        PAUSED,
        DESTROYED
    }
}
