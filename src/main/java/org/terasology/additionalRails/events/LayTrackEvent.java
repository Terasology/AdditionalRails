// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.additionalRails.events;

import org.joml.Vector3i;
import org.terasology.gestalt.entitysystem.event.Event;

public class LayTrackEvent implements Event {
    public Vector3i newRailLocation;

    public LayTrackEvent(Vector3i newRailLocation) {
        this.newRailLocation = newRailLocation;
    }
}
