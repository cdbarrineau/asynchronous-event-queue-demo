package com.cdb.eventqueue;

import com.cdb.eventqueue.event.HealthEvent;
import com.cdb.eventqueue.listener.AdvancedTestQueueListener;
import com.cdb.eventqueue.listener.BasicQueueListener;
import com.cdb.eventqueue.publisher.HealthPublisher;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is the main class to demonstrate the asynchronous queue.
 *
 * @author David Barrineau
 * @since 1/25/25 1:31 PM
 */
@Slf4j
public class Main {
    /**
     * Main entry point to the application.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {

        BasicQueueListener basicQueueListener = new BasicQueueListener();
        AdvancedTestQueueListener advancedTestQueueListener = new AdvancedTestQueueListener();

        HealthPublisher healthPublisher = new HealthPublisher("Basic Listener", basicQueueListener);
        HealthPublisher advancedHealthPublisher = new HealthPublisher("Advanced Listener", advancedTestQueueListener);

        healthPublisher.start();
        advancedHealthPublisher.start();

        Thread t1 = new Thread(() -> {
            int numReceived = 0;

            while (numReceived < HealthPublisher.NUM_EVENTS) {
                numReceived++;

                // This ill receive every event,
                HealthEvent healthEvent = basicQueueListener.waitForEvent(HealthEvent.class);

                basicQueueListener.clearEvents();

                log.info("Basic Listener received event: {}", healthEvent);
            }

            // Do a final wait to show that null is returned.
            log.info("Waiting for null event on Basic Listener.");
            HealthEvent healthEvent = basicQueueListener.waitForEvent(HealthEvent.class);

            log.info("Basic Listener received event: {}", healthEvent);
        });
        t1.setName("Basic Listener Wait Thread");
        t1.start();

        Thread t2 = new Thread(() -> {
            // This will only receive one event that matches the passed in Function.
            HealthEvent healthEvent = advancedTestQueueListener.waitForMessage(HealthEvent.class, (event) -> {

                if (event.getId() == 2) {
                    // Clear the queue.
                    advancedTestQueueListener.clearEvents();

                    return true;
                }

                return false;
            });

            log.info("Advanced Listener received event: {}", healthEvent);
        });
        t2.setName("Advanced Listener Wait Thread");
        t2.start();
    }
}
