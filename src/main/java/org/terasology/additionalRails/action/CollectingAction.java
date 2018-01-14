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

import com.google.common.collect.ImmutableList;
import org.terasology.additionalRails.components.CollectingBlockComponent;
import org.terasology.additionalRails.events.CartActivatedEvent;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
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
    @In
    private EntityManager entityManager;

    @ReceiveEvent(components={InventoryComponent.class})
    public void onActivateBlock(CartActivatedEvent event, EntityRef entity) {
        InventoryComponent inventoryComponent = entity.getComponent(InventoryComponent.class);
        int size = inventoryComponent.itemSlots.size();

        PathFollowerComponent pfComponent = entity.getComponent(PathFollowerComponent.class);
        EntityRef entityref = pfComponent.segmentMeta.association;

        BlockComponent blockcomponent = entityref.getComponent(BlockComponent.class);
        Vector3i location = new Vector3i(blockcomponent.getPosition());

        for(Vector3i v: ImmutableList.of(Vector3i.west(), Vector3i.east(), Vector3i.north(), Vector3i.south())){

            Vector3i Position = new Vector3i(location).add(v);

            EntityRef block=blockEntityRegistry.getExistingEntityAt(Position);

            CollectingBlockComponent component = block.getComponent(CollectingBlockComponent.class);

            if(component!=null){
                if(block.hasComponent(InventoryComponent.class)){
                    for (int i = 0; i < size; i++) {
                        EntityRef item = inventoryManager.getItemInSlot(entity, i);
                        if(item.exists()) {
                            int StackSize = inventoryManager.getStackSize(item);
                            Prefab prefab = item.getParentPrefab();
                            EntityRef newitem = entityManager.create(prefab);
                            for (int a = 0; a < StackSize; a++) {
                                inventoryManager.giveItem(block, block, newitem);
                            }
                            inventoryManager.removeItem(entity, block, item, true);

                        }
                    }
                }
            }
        }
    }
}
