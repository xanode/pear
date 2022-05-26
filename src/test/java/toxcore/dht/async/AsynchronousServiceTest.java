package toxcore.dht.async;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;

import static org.mockito.Mockito.*;

public class AsynchronousServiceTest {

    private AsynchronousService service;
    private AsyncTask<Object> task;

    @BeforeEach
    public void setUp() {
        service = new AsynchronousService(new LinkedBlockingDeque<>());
        task = mock(AsyncTask.class);
    }

    @Test
    void testPerfectExecution() throws Exception {
        final var result = new Object();
        when(task.call()).thenReturn(result);
        service.execute(task);

        verify(task, timeout(2000)).onPostCall(eq(result));

        final var inOrder = inOrder(task);
        inOrder.verify(task, times(1)).onPreCall();
        inOrder.verify(task, times(1)).call();
        inOrder.verify(task, times(1)).onPostCall(eq(result));

        verifyNoMoreInteractions(task);
    }

    @Test
    void testCallException() throws Exception {
        final var exception = new IOException();
        when(task.call()).thenThrow(exception);
        service.execute(task);

        verify(task, timeout(2000)).onError(eq(exception));

        final var inOrder = inOrder(task);
        inOrder.verify(task, times(1)).onPreCall();
        inOrder.verify(task, times(1)).call();
        inOrder.verify(task, times(1)).onError(exception);

        verifyNoMoreInteractions(task);
    }

    @Test
    void testPreCallException() {
        final var exception = new IllegalStateException();
        doThrow(exception).when(task).onPreCall();
        service.execute(task);

        verify(task, timeout(2000)).onError(eq(exception));

        final var inOrder = inOrder(task);
        inOrder.verify(task, times(1)).onPreCall();
        inOrder.verify(task, times(1)).onError(exception);

        verifyNoMoreInteractions(task);
    }
}
