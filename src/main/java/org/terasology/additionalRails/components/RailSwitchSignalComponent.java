// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.additionalRails.components;

import org.terasology.engine.world.block.ForceBlockActive;
import org.terasology.gestalt.entitysystem.component.EmptyComponent;

@ForceBlockActive
public class RailSwitchSignalComponent extends EmptyComponent<RailSwitchSignalComponent> {
    /**
     * this component doesn't need to have an isOn boolean field, as information about state is stored in
     * {@link org.terasology.signalling.components.SignalConsumerStatusComponent}
     */
}
