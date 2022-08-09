package the.wind.library.async;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import the.wind.library.CWHandler;

/**
 * Fake underground service which use single thread for processing task in queue sequentially and asynchronously
 * Should not use this service to process very long running task.
 * If you need long background processing, please check https://developer.android.com/guide/background
 */
public final class UndergroundService {

    // singleton instance of underground service
    @NonNull
    public static UndergroundService $ = new UndergroundService();

    // executor service
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Task is pushed to a queue
    private final Queue<Task> taskQueue = new ConcurrentLinkedQueue<>();

    // check if service is running or not
    private boolean running = false;

    // current running task
    private Task task;

    // Task handler
    private final Runnable OnTaskExecuting = new Runnable() {
        @Override
        public void run() {
            if (running) return;
            running = true;
            while ((task = taskQueue.poll()) != null) {
                // handle tasks sequentially
                task.handler.onBefore(task.params);
                task.handler.onHandle(task.params);
                task.handler.onAfter(task.params);
                task = null;
            }
            running = false;
        }
    };

    /* ---------------------- OVERRIDE ----------------------- */

    /* ---------------------- STATIC ------------------------- */

    /**
     * Register a background task.
     *
     * @param name    task name
     * @param handler handler
     * @param params  array of parameters
     * @return service
     */
    public UndergroundService register(String name, @NonNull CWHandler<Object> handler, Object... params) {
        taskQueue.add(new Task(name, handler, params)); // put task to queue
        return this;
    }

    /**
     * Start underground service
     */
    public void start() {
        if (!running) {
            executor.execute(OnTaskExecuting);
        }
    }

    /**
     * Cancel given task
     *
     * @param taskName task name
     */
    public void cancel(@NonNull String taskName) {
        Iterator<Task> it = taskQueue.iterator();
        while (it.hasNext()) {
            Task task = it.next();
            if (taskName.equals(task.name)) {
                it.remove();
                return;
            }
        }
    }

    /**
     * Check if there is any task is running in background service or not
     *
     * @return true of there is task running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Check if given task name is running in background service or not
     *
     * @param taskName task name
     * @return true if running
     */
    public boolean isRunning(@NonNull String taskName) {
        return running && task != null && taskName.equals(task.name);
    }

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Define task model
     */
    private static class Task {

        private final String name;
        private final CWHandler<Object> handler;
        private final Object[] params;

        public Task(String name, CWHandler<Object> handler, Object[] params) {
            this.name = name;
            this.handler = handler;
            this.params = params;
        }

    }
}
