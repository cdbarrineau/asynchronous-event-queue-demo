package com.cdb.eventqueue.publisher;

import com.cdb.eventqueue.event.HealthEvent;
import com.cdb.eventqueue.listener.QueueListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is a publisher of events.
 *
 * @author David Barrineau
 * @since 1/25/25 2:06 PM
 */
@Slf4j
public class HealthPublisher {

    /** The number of events for each publisher to publish. */
    public static final int NUM_EVENTS = 5;

    /** Delay of when to publish the first event, */
    private static final int DELAY = 50;

    /** Period between publishing events. */
    private static final int PERIOD = 1000;

    /** Used to publish events. */
    private final QueueListener queueListener;

    /** Used for the timer name and the event text. */
    private final String publisherName;

    /** Running count of the number of events published. */
    private int numEventsPublished;

    /** Timer used to publish events. */
    private Timer timer;


    /**
     * Constructor.
     *
     * @param publisherName Unique name for this publisher.
     * @param queueListener The listener to publish events to.
     */
    public HealthPublisher(String publisherName, QueueListener queueListener) {
        this.publisherName = publisherName;
        this.queueListener = queueListener;
    }

    /**
     * Starts publishing events.
     */
    public void start() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                numEventsPublished++;

                queueListener.addEvent(new HealthEvent(numEventsPublished, String.format("%s Event number %d", publisherName, numEventsPublished)));

                if (numEventsPublished == NUM_EVENTS) {
                    timer.cancel();
                }
            }
        };

        timer = new Timer(publisherName, true);
        timer.scheduleAtFixedRate(timerTask, DELAY, PERIOD);
    }
}
