// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.additionalRails.action;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.additionalRails.components.HarvestCartComponent;
import org.terasology.additionalRails.events.CartActivatedEvent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.engine.math.Direction;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.segmentedpaths.components.PathFollowerComponent;
import org.terasology.simpleFarming.components.BushDefinitionComponent;

@RegisterSystem(RegisterMode.AUTHORITY)
public class HarvestAction extends BaseComponentSystem {
    public int currentStage;

    @In
    private BlockEntityRegistry blockEntityRegistry;

    @ReceiveEvent(components = HarvestCartComponent.class)
    public void cartActivatedEvent(CartActivatedEvent event, EntityRef entity) {

        PathFollowerComponent pfComponent = entity.getComponent(PathFollowerComponent.class);
        EntityRef entityref = pfComponent.segmentMeta.association;

        BlockComponent blockcomponent = entityref.getComponent(BlockComponent.class);
        Vector3i location = new Vector3i(blockcomponent.getPosition(new Vector3i()));

        Direction direction = Direction.inDirection(pfComponent.heading);
        Vector3ic leftVector = direction.toSide().yawClockwise(1).direction();
        Vector3ic rightVector = direction.toSide().yawClockwise(3).direction();

        Vector3i leftPosition = new Vector3i(location).add(leftVector);
        Vector3i rightPosition = new Vector3i(location).add(rightVector);

        EntityRef block1 = blockEntityRegistry.getExistingEntityAt(leftPosition);
        EntityRef block2 = blockEntityRegistry.getExistingEntityAt(rightPosition);

        BushDefinitionComponent component1 = block1.getComponent(BushDefinitionComponent.class);
        BushDefinitionComponent component2 = block2.getComponent(BushDefinitionComponent.class);

        if (component1 != null) {
            if (component1.currentStage == component1.growthStages.size() - 1) {
                entity.send(new ActivateEvent(block1, entity, null, null, null, null, 0));
            }
        }
        if (component2 != null) {
            if (component2.currentStage == component2.growthStages.size() - 1) {
                entity.send(new ActivateEvent(block2, entity, null, null, null, null, 0));
            }
        }
    }
}
