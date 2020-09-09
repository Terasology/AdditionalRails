// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.additionalRails.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.world.block.ForceBlockActive;

@ForceBlockActive
public class RailSwitchSignalComponent implements Component {
    /**
     * this component doesn't need to have an isOn boolean field, as information about state is stored in
     * {@link org.terasology.signalling.components.SignalConsumerStatusComponent}
     */
}
