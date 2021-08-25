// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.additionalRails.components;

import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.gestalt.entitysystem.component.Component;

public class BoreDrillComponent implements Component<BoreDrillComponent> {
    public Prefab damageType;

    @Override
    public void copyFrom(BoreDrillComponent other) {
        this.damageType = other.damageType;
    }
}
