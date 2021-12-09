// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.additionalRails.action;

import org.joml.Vector3i;
import org.terasology.additionalRails.components.OnewayBoosterRailComponent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.minecarts.blocks.RailBlockFamily;
import org.terasology.minecarts.components.WrenchComponent;

/**
 * The action for inverting the direction of a OnewayBoosterRail.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class WrenchOnewayAction extends BaseComponentSystem {
    @In
    WorldProvider worldProvider;
    @In
    BlockEntityRegistry blockEntityRegistry;
    @In
    BlockManager blockManager;

    @ReceiveEvent(components = WrenchComponent.class)
    public void onRailFlipAction(ActivateEvent event, EntityRef item) {
        EntityRef targetEntity = event.getTarget();
        if (!targetEntity.hasComponent(OnewayBoosterRailComponent.class)) {
            return;
        }

        Vector3i position = targetEntity.getComponent(BlockComponent.class).getPosition(new Vector3i());

        RailBlockFamily originalFamily = (RailBlockFamily) blockManager.getBlockFamily("AdditionalRails:OnewayBoosterRail");
        RailBlockFamily invertedFamily = (RailBlockFamily) blockManager.getBlockFamily("AdditionalRails:OnewayBoosterRailInverted");

        //Using the Block from BlockComponents directly causes rails to return to their original orientation when their orientation was
        // changed because of being connected to a T or intersection.
        Block block = worldProvider.getBlock(targetEntity.getComponent(BlockComponent.class).getPosition());

        byte connections = Byte.parseByte(block.getURI().getIdentifier().toString());

        if (block.getBlockFamily() == originalFamily) {
            blockEntityRegistry.setBlockForceUpdateEntity(position, invertedFamily.getBlockByConnection(connections));
        } else if (block.getBlockFamily() == invertedFamily) {
            blockEntityRegistry.setBlockForceUpdateEntity(position, originalFamily.getBlockByConnection(connections));
        }
    }
}
