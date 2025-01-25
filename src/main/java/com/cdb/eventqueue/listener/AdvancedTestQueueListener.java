package com.cdb.eventqueue.listener;

import com.cdb.eventqueue.event.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * This class adds some advanced features to the test queue listener for not only listening for a specific message but
 * applying specific equality logic to each message. This should be used when more than one type of message will be
 * placed on this queue.
 *
 * @author David Barrineau
 * @since Jan 18, 2025 2:52:35 PM
 */
@Slf4j
public class AdvancedTestQueueListener extends QueueListener {
    /** Wait time for a message in milliseconds for a queue poll before timing out waiting for a message. */
    private static final int WAIT_TIME = 5000;

    /** Time in milliseconds for when the executor runs to check for messages on the list. */
    private static final long EXECUTOR_PERIOD = 500;

    /** The queue to place messages onto. */
    private final LinkedBlockingQueue<Event> queue = new LinkedBlockingQueue<>();


    /**
     * Clears all events and the queue.
     */
    public void clearEvents() {
        super.clearEvents();

        queue.clear();
    }

    /**
     * Waits for an event. If the event is found, it is removed from the internal collection.
     * The equality check function can be null if none is required and only the event class is
     * good enough to be tested.
     *
     * @param clazz The event type.
     * @param equalityCheck The equality check to use when testing for an event in the list.
     * This parameter can be null if only matching the event class is good enough.
     * @return Returns the event that matches type. Null is returned if time expired.
     */
    @SuppressWarnings("unchecked")
    public <T> T waitForMessage(final Class<T> clazz, final Function<T, Boolean> equalityCheck) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

        Runnable runnable = () -> {
            for (Event item : list) {
                if (item.getClass().getName().equals(clazz.getName())) {
                    boolean result = true;

                    if (Objects.nonNull(equalityCheck)) {
                        result = equalityCheck.apply((T) item);
                    }

                    if (result) {
                        list.remove(item);

                        try {
                            queue.put(item);
                        }
                        catch (InterruptedException ex) {
                            log.warn("InterruptedException putting item on queue: {}", item, ex);
                        }

                        executor.shutdownNow();

                        break;
                    }
                }
            }
        };

        executor.scheduleAtFixedRate(runnable, 0, EXECUTOR_PERIOD, TimeUnit.MILLISECONDS);

        T message = null;

        while (!executor.isShutdown()) {
            try {
                message = (T) queue.poll(WAIT_TIME, TimeUnit.MILLISECONDS);

                executor.shutdownNow();
            }
            catch (InterruptedException ex) {
                log.warn("InterruptedException polling queue.", ex);
            }
        }

        return message;
    }
}
