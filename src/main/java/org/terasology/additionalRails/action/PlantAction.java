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

import org.terasology.additionalRails.components.PlantCartComponent;
import org.terasology.additionalRails.events.CartActivatedEvent;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.math.Direction;
import org.terasology.math.JomlUtil;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.segmentedpaths.components.PathFollowerComponent;
import org.terasology.simpleFarming.components.SeedDefinitionComponent;
import org.terasology.simpleFarming.events.OnSeedPlanted;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;

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

    public void planting(EntityRef entity, Vector3i vector) {
        for (int i = 0; i <= 30; i++) {
            EntityRef myseed = inventoryManager.getItemInSlot(entity, i);
            if (myseed.hasComponent(SeedDefinitionComponent.class)) {
                SeedDefinitionComponent seedComponent = myseed.getComponent(SeedDefinitionComponent.class);
                EntityRef seed = myseed;
                EntityRef plantEntity = seedComponent.prefab == null ? seed : entityManager.create(seedComponent.prefab);
                plantEntity.send(new OnSeedPlanted(JomlUtil.from(vector)));
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
        Vector3i location = new Vector3i(blockcomponent.getPosition());

        Direction direction = Direction.inDirection(JomlUtil.from(pfComponent.heading));
        Vector3i leftVector = direction.toSide().yawClockwise(1).getVector3i();
        Vector3i rightVector = direction.toSide().yawClockwise(3).getVector3i();

        Vector3i leftPosition = new Vector3i(location).add(leftVector);
        Vector3i rightPosition = new Vector3i(location).add(rightVector);

        Vector3i leftdownPosition = new Vector3i(leftPosition).add(Vector3i.down());
        Vector3i rightdownPosition = new Vector3i(rightPosition).add(Vector3i.down());

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
