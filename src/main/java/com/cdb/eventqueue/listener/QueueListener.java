package com.cdb.eventqueue.listener;

import com.cdb.eventqueue.event.Event;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is the base class for all queue listeners.
 *
 * @author davidbarrineau - david.barrineau@phxlogistics.com
 * @since 1/25/25 3:05 PM
 */
@Slf4j
public abstract class QueueListener {

    /** The list messages are placed on that we will check. */
    protected final CopyOnWriteArrayList<Event> list = new CopyOnWriteArrayList<>();

    /**
     * This method will add an event to the list.
     *
     * @param event The event to handle
     */
    public void addEvent(final Event event) {
        list.add(event);
    }

    /**
     * Clears all events.
     */
    public void clearEvents() {
        list.clear();
    }
}
