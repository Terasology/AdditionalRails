package org.terasology.additionalRails.components;

import org.terasology.entitySystem.Component;

public class RailSwitchComponent implements Component {
    public boolean isOn;
    public boolean hasSignal;
    public int mode;
}
