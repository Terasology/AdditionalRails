// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.additionalRails.ui;

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.logic.characters.CharacterComponent;
import org.terasology.logic.players.LocalPlayer;
import org.terasology.registry.In;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.nui.databinding.ReadOnlyBinding;
import org.terasology.rendering.nui.layers.ingame.inventory.InventoryGrid;

/**
 * Screen opened up upon activation of the Track Layer Cart - used to put rails into it.
 * @author not anuar2k (ha! got ya!), but the ContainerScreen.java's creator. It's nearly a 1:1 copy.
 */
public class TrackLayerCartScreen extends CoreScreenLayer {

    @In
    private LocalPlayer localPlayer;

    private InventoryGrid containerInventory;

    @Override
    public void initialise() {
        InventoryGrid inventory = find("inventory", InventoryGrid.class);
        inventory.bindTargetEntity(new ReadOnlyBinding<EntityRef>() {
            @Override
            public EntityRef get() {
                return localPlayer.getCharacterEntity();
            }
        });
        inventory.setCellOffset(10);

        containerInventory = find("container", InventoryGrid.class);
        containerInventory.bindTargetEntity(new ReadOnlyBinding<EntityRef>() {
            @Override
            public EntityRef get() {
                EntityRef characterEntity = localPlayer.getCharacterEntity();
                CharacterComponent characterComponent = characterEntity.getComponent(CharacterComponent.class);
                return characterComponent.predictedInteractionTarget;
            }
        });
    }

    @Override
    public boolean isModal() {
        return false;
    }
}
