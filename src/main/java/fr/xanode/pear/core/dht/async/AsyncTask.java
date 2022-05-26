package fr.xanode.pear.core.dht.async;

import java.util.concurrent.Callable;

public interface AsyncTask<O> extends Callable<O> {
    /**
     * Is called in context of caller thread before call to {@link #call()}. Small tasks can be
     * performed here so that the performance penalty of context switching is not incurred in case
     * of invalid requests.
     */
    void onPreCall();

    /**
     * A callback called after the result is successfully computed by {@link #call()}. This
     * method is called in context of background thread.
     */
    void onPostCall(O result);

    /**
     * A callback called if computing the task resulted in some exception. This method is called
     * when either of {@link #call()} or {@link #onPreCall()} throw any exception.
     *
     * @param throwable error cause
     */
    void onError(Throwable throwable);

    /**
     * This method is called in context of background thread. The computation of task reside here.
     */
    @Override
    O call() throws Exception;
}
