package fr.xanode.pear.core.dht.async;

import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsynchronousService {
    /**
     * This represents the queuing layer as well as synchronous layer of the patterns. The thread pool
     * contains worker threads which execute the tasks in blocking/synchronous manner. Long running
     * tasks are performed in the background which does not affect the performance of main thread.
     */
    private final ExecutorService service;

    /**
     * Creates an asynchronous service using {@code workQueue} as communication channel between
     * asynchronous layer and synchronous layer.
     */
    public AsynchronousService(BlockingQueue<Runnable> workQueue) {
        this.service = new ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS, workQueue);
    }

    /**
     * A non-blocking method which performs the task provided in the background and returns immediately.
     * On successful completion of the task the result is posted back using callback method {@link
     * AsyncTask#onPostCall(Object)}, if task execution is unable to complete normally due to some
     * exception then the reason for error is posted back using callback method {@link AsyncTask#onError(Throwable)}.
     */
    public <T> void execute(final AsyncTask<T> task) {
        try {
            // Execute small tasks
            task.onPreCall();
        } catch (Exception e) {
            task.onError(e);
            return;
        }

        service.submit(new FutureTask<>(task) {
            @Override
            protected void done() {
                super.done();
                try {
                    // Called in context of background thread
                    task.onPostCall(this.get());
                } catch (InterruptedException ignored) {
                    // Shouldn't occur
                } catch (ExecutionException e) {
                    task.onError(e.getCause());
                }
            }
        });
    }

    /**
     * Stops the pool of workers. This is blocking call to wait for all tasks to be completed.
     */
    public void close() {
        service.shutdown();
        try {
            service.awaitTermination(10, TimeUnit.SECONDS);
        } catch(InterruptedException e) {
            log.error("Error waiting for executor service shutdown!");
        }
    }

}
