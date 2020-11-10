package org.terasology.additionalRails.events;

import org.joml.Vector3i;
import org.terasology.entitySystem.event.Event;

public class LayTrackEvent implements Event {
    public Vector3i newRailLocation;

    public LayTrackEvent(Vector3i newRailLocation) {
        this.newRailLocation = newRailLocation;
    }
}
