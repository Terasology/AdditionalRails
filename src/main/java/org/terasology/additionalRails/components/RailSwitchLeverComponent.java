// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.additionalRails.components;

import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.gestalt.entitysystem.component.Component;

@ForceBlockActive
public class RailSwitchLeverComponent implements Component<RailSwitchLeverComponent> {
    /**
     * determines if the block is in on or off state
     */
    public boolean isOn;

    @Override
    public void copy(RailSwitchLeverComponent other) {
        this.isOn = other.isOn;
    }
}
