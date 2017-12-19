package org.terasology.additionalRails.components;

import org.terasology.entitySystem.Component;
import org.terasology.world.block.ForceBlockActive;

@ForceBlockActive
public class RailSwitchSignalComponent implements Component {
    /**
     * this component doesn't need to have an isOn boolean field, as information about state is stored in {@link org.terasology.signalling.components.SignalConsumerStatusComponent}
     */
}
