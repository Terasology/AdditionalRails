// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.additionalRails.events;

import org.terasology.engine.entitySystem.event.Event;
import org.terasology.math.geom.Vector3i;

public class LayTrackEvent implements Event {
    public Vector3i newRailLocation;

    public LayTrackEvent(Vector3i newRailLocation) {
        this.newRailLocation = newRailLocation;
    }
}
