package com.cdb.eventqueue.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class is a simple event that gets published.
 *
 * @author davidbarrineau
 * @since 1/25/25 1:57 PM
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthEvent extends Event {

    /** Simulates some ID like a Primary Key. */
    private long id;

    /** Some piece of data for the event. */
    private String name;
}
