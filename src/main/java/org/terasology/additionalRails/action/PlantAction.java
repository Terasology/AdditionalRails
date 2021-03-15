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

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.additionalRails.components.PlantCartComponent;
import org.terasology.additionalRails.events.CartActivatedEvent;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.module.inventory.systems.InventoryManager;
import org.terasology.engine.math.Direction;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.segmentedpaths.components.PathFollowerComponent;
import org.terasology.simpleFarming.components.SeedDefinitionComponent;
import org.terasology.simpleFarming.events.OnSeedPlanted;

@RegisterSystem(RegisterMode.AUTHORITY)
public class PlantAction extends BaseComponentSystem {

    private Block airBlock;

    @In
    private WorldProvider worldprovider;

    @In
    private BlockManager blockManager;

    @In
    private InventoryManager inventoryManager;

    @In
    private EntityManager entityManager;

    public void planting(EntityRef entity, Vector3ic vector) {
        for (int i = 0; i <= 30; i++) {
            EntityRef myseed = inventoryManager.getItemInSlot(entity, i);
            if (myseed.hasComponent(SeedDefinitionComponent.class)) {
                SeedDefinitionComponent seedComponent = myseed.getComponent(SeedDefinitionComponent.class);
                EntityRef seed = myseed;
                EntityRef plantEntity = seedComponent.prefab == null ? seed : entityManager.create(seedComponent.prefab);
                plantEntity.send(new OnSeedPlanted(vector));
                inventoryManager.removeItem(seed.getOwner(), seed, seed, true, 1);
                break;

            }
        }
    }

    @ReceiveEvent(components = {PlantCartComponent.class})
    public void cartActivatedEvent(CartActivatedEvent event, EntityRef entity) {
        airBlock = blockManager.getBlock(BlockManager.AIR_ID);

        PathFollowerComponent pfComponent = entity.getComponent(PathFollowerComponent.class);
        EntityRef entityref = pfComponent.segmentMeta.association;

        BlockComponent blockcomponent = entityref.getComponent(BlockComponent.class);
        Vector3i location = blockcomponent.getPosition(new Vector3i());

        Direction direction = Direction.inDirection(pfComponent.heading);
        Vector3ic leftVector = direction.toSide().yawClockwise(1).direction();
        Vector3ic rightVector = direction.toSide().yawClockwise(3).direction();

        Vector3i leftPosition = new Vector3i(location).add(leftVector);
        Vector3i rightPosition = new Vector3i(location).add(rightVector);

        Vector3i leftdownPosition = leftPosition.add(0, -1, 0, new Vector3i());
        Vector3i rightdownPosition = rightPosition.add(0, -1, 0, new Vector3i());

        Block leftdown = worldprovider.getBlock(leftdownPosition);
        Block rightdown = worldprovider.getBlock(rightdownPosition);
        Block left = worldprovider.getBlock(leftPosition);
        Block right = worldprovider.getBlock(rightPosition);

        if (leftdown != airBlock) {
            if (left == airBlock) {
                planting(entity, leftPosition);
            }

        }
        if (rightdown != airBlock) {
            if (right == airBlock) {
                planting(entity, rightPosition);
            }
        }
    }
}
