package the.wind.library.async;

import android.os.Handler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.Nullable;

public class WindAsyncTask<Params, Progress, Result> implements Promise.IPromise<Result> {

    // Thread, promise
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Promise<Result> mPromise = new Promise<>();

    // UI thread handler
    @Nullable
    private Handler UIThread;

    // result and exception
    private Params[] params;
    private Exception exception;
    private Result result;

    // flag
    private boolean cancel = false;
    private boolean running = false;

    // On progress handler
    private final OnProgressHandler<Progress> OnProgressHandler = new OnProgressHandler<Progress>() {
        @Override
        protected void onProgressUpdate(Iterable<Progress> values) {
            super.onProgressUpdate(values);
            WindAsyncTask.this.onProgressUpdate(values);
        }
    };

    // On post result handler
    private final Runnable OnPostResultHandler = () -> {
        onPostExecute(result, exception);
        running = false; // finish the task
    };

    // On execute background thread handler
    private final Runnable OnExecuteThreadHandler = () -> {
        try {
            if (!cancel) {
                result = doInBackground(params);
            }
            exception = null;
        } catch (Exception ex) {
            exception = ex;
        }
        if (!cancel && UIThread != null) {
            UIThread.post(OnPostResultHandler);
        } else {
            // finish the task
            running = false;
        }
    };

    /**
     * Default constructor
     */
    private WindAsyncTask() {
    }

    /**
     * Constructor
     *
     * @param uiThread UI thread handler
     */
    public WindAsyncTask(@Nullable Handler uiThread) {
        this.UIThread = uiThread;
    }

    /* ---------------------- OVERRIDE ----------------------- */

    @Override
    public Promise<Result> promise() {
        return mPromise;
    }

    /* ---------------------- STATIC ------------------------- */

    /* ---------------------- ABSTRACT ----------------------- */

    /**
     * Do in background thread
     *
     * @param params parameters
     * @return result
     */
    @SuppressWarnings("unchecked")
    protected Result doInBackground(Params... params) throws Exception {
        return null;
    }

    /**
     * On progress update, which runs on main UI thread
     *
     * @param values progress values
     */
    protected void onProgressUpdate(Iterable<Progress> values) {

    }

    /**
     * On pre-execute
     */
    protected void onPreExecute() {
        // do nothing
    }

    /**
     * On post executed which run on main UI thread
     *
     * @param result result
     */
    protected void onPostExecute(Result result, Exception exception) {
        if (exception != null) {
            mPromise.resolve(exception);
        } else {
            mPromise.resolve(result);
        }
    }

    /* ---------------------- GET-SET ------------------------ */

    /* ---------------------- METHOD ------------------------- */

    /**
     * Public progress result
     *
     * @param progressResult progress result
     */
    @SafeVarargs
    protected final void publishProgress(Progress... progressResult) {
        if (!cancel && UIThread != null) {
            OnProgressHandler.addAll(Arrays.asList(progressResult));
            UIThread.post(OnProgressHandler);
        }
    }

    /**
     * Execute
     *
     * @param params parameters
     */
    @SafeVarargs
    public final WindAsyncTask<Params, Progress, Result> execute(Params... params) {
        if (isRunning()) {
            // The task is running -> cannot execute again
            return this;
        }
        cancel = false;
        running = true;
        this.params = params;
        onPreExecute();
        executor.execute(OnExecuteThreadHandler);
        return this;
    }

    /**
     * Destroy async task
     */
    public void cancel() {
        cancel = true;
    }

    /**
     * Check if task is running or not
     *
     * @return true if running, else false
     */
    public boolean isRunning() {
        return running;
    }

    /* ---------------------- INNER CLASS -------------------- */

    /**
     * Handle data which is published in progress
     */
    private static class OnProgressHandler<Data> implements Runnable {

        private boolean running = false;
        private final Queue<Data> dataQueue = new LinkedList<>();
        private final List<Data> resultSet = new LinkedList<>();

        @Override
        public void run() {
            if (running || dataQueue.isEmpty()) {
                return;
            }
            running = true;
            resultSet.clear();
            Data rs;
            while ((rs = dataQueue.poll()) != null) {
                resultSet.add(rs);
            }
            onProgressUpdate(resultSet);
            running = false;
            run();
        }

        /**
         * On progress data update
         *
         * @param values values
         */
        protected void onProgressUpdate(Iterable<Data> values) {

        }

        /**
         * Add all data
         *
         * @param dataList list of data
         */
        public void addAll(List<Data> dataList) {
            dataQueue.addAll(dataList);
        }
    }

}
