package com.cdb.eventqueue.listener;

import com.cdb.eventqueue.event.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * This class uses a basic sleep to check for only objects of a certain class.
 *
 * @author David Barrineau
 * @since 1/25/25 2:10 PM
 */
@Slf4j
public class BasicQueueListener extends QueueListener {

    /** Number of iterations to wait for a message before timing out. */
    public static final int NUM_ITERS = 5;

    /** Half a second in milliseconds */
    private static final int HALF_SECOND = 500;

    /**
     * Waits for an event.  This is a basic implementation where only the class
     * of the event is checked.
     *
     * @param clazz The event type to check.
     * @return Returns the event that matches type. Null is returned if time expired.
     */
    @SuppressWarnings("unchecked")
    public <T> T waitForEvent(final Class<T> clazz) {
        T result = null;

        final String className = clazz.getName();

        for (int i = 0; i < NUM_ITERS; i++) {
            for (Event event : list) {
                if (event.getClass().getName().equals(className)) {
                    log.debug("Found event in received events of type: {}", event.getClass().getName());

                    result = (T) event;
                    return result;
                }
                else {
                    log.info("Have an event but incorrect type.  Event class: {} Waiting for event of type: {}", event.getClass().getName(), className);
                }
            }

            try {
                TimeUnit.MILLISECONDS.sleep(HALF_SECOND);
            }
            catch (Exception ex) {
                log.error("Error waiting for message of type: {}", className, ex);
            }
        }

        log.error("Timeout waiting for event of type: {}, all received messages: {}", className, list);

        return result;
    }
}
