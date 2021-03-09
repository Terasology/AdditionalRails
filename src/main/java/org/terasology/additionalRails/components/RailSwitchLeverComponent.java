package org.terasology.additionalRails.components;

import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.world.block.ForceBlockActive;

@ForceBlockActive
public class RailSwitchLeverComponent implements Component {
    /**
     * determines if the block is in on or off state
     */
    public boolean isOn;
}
