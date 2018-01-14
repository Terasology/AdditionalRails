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
import org.terasology.additionalRails.components.CollectingBlockComponent;
import org.terasology.additionalRails.events.CartActivatedEvent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.segmentedpaths.components.PathFollowerComponent;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.BlockComponent;

@RegisterSystem(RegisterMode.AUTHORITY)
public class CollectingAction extends BaseComponentSystem {
    @In
    private InventoryManager inventoryManager;
    @In
    private BlockEntityRegistry blockEntityRegistry;

    @ReceiveEvent(components={InventoryComponent.class})
    public void onActivateBlock(CartActivatedEvent event, EntityRef entity) {

        PathFollowerComponent pfComponent = entity.getComponent(PathFollowerComponent.class);
        EntityRef entityref = pfComponent.segmentMeta.association;

        BlockComponent blockcomponent = entityref.getComponent(BlockComponent.class);
        Vector3i location = new Vector3i(blockcomponent.getPosition());

        Vector3i leftPosition = new Vector3i(location).add(Vector3i.west());
        Vector3i rightPosition = new Vector3i(location).add(Vector3i.east());
        Vector3i frontPosition = new Vector3i(location).add(Vector3i.north());
        Vector3i backPosition = new Vector3i(location).add(Vector3i.south());

        EntityRef block1=blockEntityRegistry.getExistingEntityAt(leftPosition);
        EntityRef block2=blockEntityRegistry.getExistingEntityAt(rightPosition);
        EntityRef block3=blockEntityRegistry.getExistingEntityAt(frontPosition);
        EntityRef block4=blockEntityRegistry.getExistingEntityAt(backPosition);

        CollectingBlockComponent component1 = block1.getComponent(CollectingBlockComponent.class);
        CollectingBlockComponent component2 = block2.getComponent(CollectingBlockComponent.class);
        CollectingBlockComponent component3 = block3.getComponent(CollectingBlockComponent.class);
        CollectingBlockComponent component4 = block4.getComponent(CollectingBlockComponent.class);

        if(component1!=null){
            if(block1.hasComponent(InventoryComponent.class)){
                for (int i = 0; i < 30; i++) {
                    EntityRef item = inventoryManager.getItemInSlot(entity, i);
                    if(item.exists()){
                        inventoryManager.moveItem(entity,block1,i,block1,i,1);
                        inventoryManager.removeItem(entity,block1,item,true);
                    }
                }
            }
        }else if(component2!=null){
            if(block2.hasComponent(InventoryComponent.class)) {
                for (int i = 0; i < 30; i++) {
                    EntityRef item = inventoryManager.getItemInSlot(entity, i);
                    if (item.exists()) {
                        inventoryManager.moveItem(entity,block2,i,block2,i,1);
                        inventoryManager.removeItem(entity, block2, item, true);
                    }
                }
            }
        }else if(component3!=null){
            if(block3.hasComponent(InventoryComponent.class)) {
                for (int i = 0; i < 30; i++) {
                    EntityRef item = inventoryManager.getItemInSlot(entity, i);
                    if (item.exists()) {
                        inventoryManager.moveItem(entity,block3,i,block3,i,1);
                        inventoryManager.removeItem(entity, block3, item, true);
                    }
                }
            }
        }else if(component4!=null) {
            if (block4.hasComponent(InventoryComponent.class)) {
                for (int i = 0; i < 30; i++) {
                    EntityRef item = inventoryManager.getItemInSlot(entity, i);
                    if (item.exists()) {
                        inventoryManager.moveItem(entity,block4,i,block4,i,1);
                        inventoryManager.removeItem(entity, block4, item, true);
                    }
                }
            }
        }
    }
}
