/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.additionalRails.action;

import org.terasology.additionalRails.components.OnewayBoosterRailComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.math.SideBitFlag;
import org.terasology.math.geom.Vector3i;
import org.terasology.minecarts.blocks.RailsUpdateFamily;
import org.terasology.minecarts.components.WrenchComponent;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;

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

    @ReceiveEvent(components = {WrenchComponent.class})
    public void onRailFlipAction(ActivateEvent event, EntityRef item)
    {
        EntityRef targetEntity = event.getTarget();
        if (!targetEntity.hasComponent(OnewayBoosterRailComponent.class))
            return;

        Vector3i position = targetEntity.getComponent(BlockComponent.class).getPosition();

        RailsUpdateFamily railFamily = (RailsUpdateFamily) blockManager.getBlockFamily("AdditionalRails:OnewayBoosterRail");
        RailsUpdateFamily invertFamily = (RailsUpdateFamily) blockManager.getBlockFamily("AdditionalRails:OnewayBoosterRailInverted");

        Block block = worldProvider.getBlock(targetEntity.getComponent(BlockComponent.class).getPosition());

        byte connections = Byte.parseByte(block.getURI().getIdentifier().toString());

        if(SideBitFlag.getSides(connections).size() <= 3) {
            if (block.getBlockFamily() == railFamily) {
                blockEntityRegistry.setBlockForceUpdateEntity(position, invertFamily.getBlockByConnection(connections));
            }
            else if(block.getBlockFamily() == invertFamily) {
                blockEntityRegistry.setBlockForceUpdateEntity(position, railFamily.getBlockByConnection(connections));
            }
        }
    }
}