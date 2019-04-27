package org.terasology.additionalRails.events;

import org.terasology.entitySystem.event.Event;
import org.terasology.math.geom.Vector3i;
import org.terasology.minecarts.blocks.RailBlockFamily;

public class LayTrackEvent implements Event {
    public RailBlockFamily ruFamily;
    public Vector3i newRailLocation;

    public LayTrackEvent() {
    }

    public LayTrackEvent(RailBlockFamily ruFamily, Vector3i newRailLocation) {
        this.ruFamily = ruFamily;
        this.newRailLocation = newRailLocation;
    }
}
