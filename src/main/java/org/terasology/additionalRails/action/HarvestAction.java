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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.additionalRails.components.HarvestCartComponent;
import org.terasology.additionalRails.events.CartActivatedEvent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.math.*;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.segmentedpaths.components.PathFollowerComponent;
import org.terasology.simpleFarming.components.BushDefinitionComponent;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.BlockComponent;

@RegisterSystem(RegisterMode.AUTHORITY)
public class HarvestAction extends BaseComponentSystem{

    public int currentStage;
    private static final Logger logger = LoggerFactory.getLogger(HarvestAction.class);
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @ReceiveEvent(components = {HarvestCartComponent.class})
    public void cartActivatedEvent(CartActivatedEvent event, EntityRef entity) {
        logger.info("Activated Harvest Cart ID: {}", entity.getId());

        PathFollowerComponent pfComp = entity.getComponent(PathFollowerComponent.class);
        EntityRef rbEntity = pfComp.segmentMeta.association;

        BlockComponent bComp = rbEntity.getComponent(BlockComponent.class);
        Vector3i rbLocation = new Vector3i(bComp.getPosition());

        Direction direction = Direction.inDirection(pfComp.heading);
        Vector3i leftVector = direction.toSide().yawClockwise(1).getVector3i();
        Vector3i rightVector = direction.toSide().yawClockwise(3).getVector3i();

        Vector3i firstBlockPosition = new Vector3i(rbLocation).add(leftVector);
        Vector3i secondBlockPosition = new Vector3i(rbLocation).add(rightVector);



        EntityRef block1=blockEntityRegistry.getExistingEntityAt(firstBlockPosition);
        EntityRef block2=blockEntityRegistry.getExistingEntityAt(secondBlockPosition);
        BushDefinitionComponent component1 = block1.getComponent(BushDefinitionComponent.class);
        BushDefinitionComponent component2 = block2.getComponent(BushDefinitionComponent.class);
        if(component1!=null){
            if(component1.currentStage==1){
                entity.send(new ActivateEvent(block1, entity, null, null, null, null, 0));
            }
        }
        if(component2!=null){
            if(component1.currentStage==1){
                entity.send(new ActivateEvent(block2, entity, null, null, null, null, 0));
            }
        }
    }
}

