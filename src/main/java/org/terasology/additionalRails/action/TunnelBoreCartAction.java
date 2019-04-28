/*
 * Copyright 2018 MovingBlocks
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

import java.util.ArrayList;
import java.util.List;

import org.terasology.additionalRails.components.BoreDrillComponent;
import org.terasology.additionalRails.components.TrackLayerCartComponent;
import org.terasology.additionalRails.components.TunnelBoreCartComponent;
import org.terasology.additionalRails.events.LayTrackEvent;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.health.DoDamageEvent;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.events.BeforeItemPutInInventory;
import org.terasology.math.Side;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.segmentedpaths.components.PathFollowerComponent;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;

@RegisterSystem(RegisterMode.AUTHORITY)
public class TunnelBoreCartAction extends BaseComponentSystem {

    @In
    private EntityManager entityManager;
    @In
    private WorldProvider worldProvider;
    @In
    private BlockEntityRegistry blockEntityRegistry;

    @ReceiveEvent(priority=EventPriority.PRIORITY_HIGH)
    public void onLayTrack(LayTrackEvent event, EntityRef cart, TrackLayerCartComponent comp, TunnelBoreCartComponent boreComp) {
        Random rand = new FastRandom();

        BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        Block air = blockManager.getBlock(BlockManager.AIR_ID);

        Vector3i cartLoc = cart.getComponent(PathFollowerComponent.class).segmentMeta.association.getComponent(BlockComponent.class).position;
        Vector3i direction = new Vector3i(event.newRailLocation).sub(cartLoc);
        Side facing = Side.inDirection(direction.getX(), 0, direction.getZ());
        Vector3i perp = facing.yawClockwise(1).getVector3i();

        List<Vector3i> excavate = new ArrayList<>();
        excavate.add(new Vector3i(event.newRailLocation));
        excavate.add(new Vector3i(event.newRailLocation).add(perp));
        excavate.add(new Vector3i(event.newRailLocation).sub(perp));
        excavate.add(new Vector3i(event.newRailLocation).addY(1));
        excavate.add(new Vector3i(event.newRailLocation).add(perp).addY(1));
        excavate.add(new Vector3i(event.newRailLocation).sub(perp).addY(1));
        excavate.add(new Vector3i(event.newRailLocation).addY(2));
        excavate.add(new Vector3i(event.newRailLocation).add(perp).addY(2));
        excavate.add(new Vector3i(event.newRailLocation).sub(perp).addY(2));

        EntityRef boreDrill = cart.getComponent(InventoryComponent.class).itemSlots.get(3);
        Prefab damageType = boreDrill == EntityRef.NULL ? entityManager.create("engine:physicalDamage").getParentPrefab() :
            boreDrill.getComponent(BoreDrillComponent.class).damageType;

        Vector3i loc = excavate.get(rand.nextInt(excavate.size()));
        if(!worldProvider.getBlock(loc).equals(air) &&
                !worldProvider.getBlock(loc).getBlockFamily().equals(event.ruFamily)) {
            EntityRef blockEntity = blockEntityRegistry.getBlockEntityAt(loc);
            blockEntity.send(new DoDamageEvent(1, damageType, cart, boreDrill));
        }
    }

    @ReceiveEvent
    public void onFilterDrill(BeforeItemPutInInventory event, EntityRef cart, TunnelBoreCartComponent comp) {
        if(event.getSlot() == 3 && !event.getItem().hasComponent(BoreDrillComponent.class)) {
            event.consume();
        }
    }
}
