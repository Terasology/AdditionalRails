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
import org.terasology.additionalRails.components.PlantCartComponent;
import org.terasology.additionalRails.events.CartActivatedEvent;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.PrefabManager;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.inventory.InventoryManager;
import org.terasology.math.Direction;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.segmentedpaths.components.PathFollowerComponent;
import org.terasology.simpleFarming.components.SeedDefinitionComponent;
import org.terasology.simpleFarming.events.OnSeedPlanted;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;

@RegisterSystem(RegisterMode.AUTHORITY)
public class PlantAction extends BaseComponentSystem {
    private Block airBlock;

    private static final Logger logger = LoggerFactory.getLogger(PlantAction.class);
    @In
    private WorldProvider worldprovider;

    @In
    private BlockEntityRegistry blockEntityRegistry;

    @In
    private BlockManager blockManager;

    @In
    private InventoryManager inventoryManager;

    @In
    private EntityManager entityManager;

    @In
    private PrefabManager prefabManager;

    @ReceiveEvent(components = {PlantCartComponent.class})
    public void cartActivatedEvent(CartActivatedEvent event, EntityRef entity) {
        airBlock = blockManager.getBlock(BlockManager.AIR_ID);
        logger.info("Activated Example Activable Cart ID: {}", entity.getId());

        PathFollowerComponent pfComponent = entity.getComponent(PathFollowerComponent.class);
        EntityRef entityref = pfComponent.segmentMeta.association;

        BlockComponent blockcomponent = entityref.getComponent(BlockComponent.class);
        Vector3i location = new Vector3i(blockcomponent.getPosition());

        Direction direction = Direction.inDirection(pfComponent.heading);
        Vector3i leftVector = direction.toSide().yawClockwise(1).getVector3i();
        Vector3i rightVector = direction.toSide().yawClockwise(3).getVector3i();

        Vector3i leftPosition = new Vector3i(location).add(leftVector).add(Vector3i.down());
        Vector3i rightPosition = new Vector3i(location).add(rightVector).add(Vector3i.down());

        Block a=worldprovider.getBlock(leftPosition);
        logger.info(a.getDisplayName());
        Block b=worldprovider.getBlock(rightPosition);
        logger.info(b.getDisplayName());

        if(a.getDisplayName()!= airBlock.getDisplayName() ){
            logger.info("left");
            for(int i=1; i<=31; i++){
                EntityRef myseed = inventoryManager.getItemInSlot(entity, i);
                if(myseed.hasComponent(SeedDefinitionComponent.class)){
                    logger.info("l");
                    EntityRef seed =myseed;
                    SeedDefinitionComponent seedComponent= new SeedDefinitionComponent();
                    EntityRef plantEntity = seedComponent.prefab == null ? seed : entityManager.create(seedComponent.prefab);
                    plantEntity.send(new OnSeedPlanted(leftPosition));
                    inventoryManager.removeItem(seed.getOwner(), seed, seed, true, 1);
                    break;
                }
            }
        }
        if(b.getDisplayName()!= airBlock.getDisplayName() ){
            logger.info("right");
            for(int i=1; i<=31; i++){
                EntityRef myseed = inventoryManager.getItemInSlot(entity, i);
                if(myseed.hasComponent(SeedDefinitionComponent.class)){
                    logger.info("r");
                    EntityRef seed =myseed;
                    SeedDefinitionComponent seedComponent= new SeedDefinitionComponent();
                    EntityRef plantEntity = seedComponent.prefab == null ? seed : entityManager.create(seedComponent.prefab);
                    plantEntity.send(new OnSeedPlanted(rightPosition));
                    inventoryManager.removeItem(seed.getOwner(), seed, seed, true, 1);
                    break;
                }
            }

        }
    }
}

