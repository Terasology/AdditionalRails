// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.additionalRails.components;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Component used to differentiate Hoover Cart from other ones.
 */
public class HooverCartComponent implements Component<HooverCartComponent> {
    public int energy = 0;

    @Override
    public void copyFrom(HooverCartComponent other) {
        this.energy = other.energy;
    }
}
