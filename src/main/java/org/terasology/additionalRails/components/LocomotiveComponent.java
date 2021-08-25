// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.additionalRails.components;

import org.terasology.gestalt.entitysystem.component.Component;

public class LocomotiveComponent implements Component<LocomotiveComponent> {
    public boolean active = false;

    @Override
    public void copyFrom(LocomotiveComponent other) {
        this.active = other.active;
    }
}
