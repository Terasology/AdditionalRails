// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.additionalRails.components;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Component differentiating Explosive Cart.
 */
public class ExplosiveCartComponent implements Component<ExplosiveCartComponent> {
    /**
     * Defines how long does it take to make the cart explode after being activated by Activator Rail.
     */
    public long fuseLengthMs = 4000;

    @Override
    public void copyFrom(ExplosiveCartComponent other) {
        this.fuseLengthMs = other.fuseLengthMs;
    }
}
