package org.terasology.additionalRails.events;

import org.terasology.entitySystem.event.Event;
import org.terasology.math.geom.Vector3i;

public class LayTrackEvent implements Event {
    public Vector3i newRailLocation;

    public LayTrackEvent(Vector3i newRailLocation) {
        this.newRailLocation = newRailLocation;
    }
}
