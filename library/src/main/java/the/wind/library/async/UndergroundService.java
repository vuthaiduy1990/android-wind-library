package the.wind.library.async;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
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
    private final Queue<CWHandler<Object>> taskQueue = new ConcurrentLinkedQueue<>();
    // Map between task name and parameters
    private final Map<String, Object[]> dataMap = new LinkedHashMap<>();

    // check if service is running or not
    private boolean running = false;

    // current running task
    private CWHandler<Object> task;

    // Task handler
    private final Runnable OnTaskExecuting = new Runnable() {
        @Override
        public void run() {
            if (running) return;
            running = true;
            while ((task = taskQueue.poll()) != null) {
                // handle tasks sequentially
                Object[] params = dataMap.remove(task.getTaskName());
                task.onBefore(params);
                task.onHandle(params);
                task.onAfter(params);
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
     * @param task background task
     * @param data array of data
     * @return service
     */
    public UndergroundService register(@NonNull CWHandler<Object> task, Object... data) {
        taskQueue.add(task); // put task to queue
        dataMap.put(task.getTaskName(), data);
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
        Iterator<CWHandler<Object>> it = taskQueue.iterator();
        while (it.hasNext()) {
            CWHandler<?> task = it.next();
            if (taskName.equals(task.getTaskName())) {
                it.remove();
                dataMap.remove(task.getTaskName());
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
        return running && task != null && taskName.equals(task.getTaskName());
    }

    /* ---------------------- ABSTRACT ----------------------- */

    /* ---------------------- GET-SET ------------------------ */

    /* ---------------------- METHOD ------------------------- */

    /* ---------------------- INNER CLASS -------------------- */
}
