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

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.additionalRails.components.BoreDrillComponent;
import org.terasology.additionalRails.components.TrackLayerCartComponent;
import org.terasology.additionalRails.components.TunnelBoreCartComponent;
import org.terasology.additionalRails.events.LayTrackEvent;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.EventPriority;
import org.terasology.engine.entitySystem.event.Priority;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.math.Side;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.registry.In;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.minecarts.blocks.RailComponent;
import org.terasology.module.health.events.DoDamageEvent;
import org.terasology.module.inventory.components.InventoryComponent;
import org.terasology.module.inventory.events.BeforeItemPutInInventory;
import org.terasology.segmentedpaths.components.PathFollowerComponent;

import java.util.ArrayList;
import java.util.List;

@RegisterSystem(RegisterMode.AUTHORITY)
public class TunnelBoreCartAction extends BaseComponentSystem {

    @In
    private EntityManager entityManager;
    @In
    private WorldProvider worldProvider;
    @In
    private BlockEntityRegistry blockEntityRegistry;

    @Priority(EventPriority.PRIORITY_HIGH)
    @ReceiveEvent
    public void onLayTrack(LayTrackEvent event, EntityRef cart, TrackLayerCartComponent comp, TunnelBoreCartComponent boreComp) {
        Random rand = new FastRandom();

        BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        Block air = blockManager.getBlock(BlockManager.AIR_ID);

        Vector3i cartLoc = cart.getComponent(PathFollowerComponent.class).segmentMeta.association.getComponent(BlockComponent.class).getPosition(new Vector3i());
        Vector3i direction = new Vector3i(event.newRailLocation).sub(cartLoc);
        Side facing = Side.inDirection(direction.x(), 0, direction.z());
        Vector3ic perp = facing.yawClockwise(1).direction();

        List<Vector3i> excavate = new ArrayList<>();
        excavate.add(new Vector3i(event.newRailLocation));
        excavate.add(new Vector3i(event.newRailLocation).add(perp));
        excavate.add(new Vector3i(event.newRailLocation).sub(perp));
        excavate.add(new Vector3i(event.newRailLocation).add(0, 1, 0));
        excavate.add(new Vector3i(event.newRailLocation).add(perp).add(0, 1, 0));
        excavate.add(new Vector3i(event.newRailLocation).sub(perp).add(0, 1, 0));
        excavate.add(new Vector3i(event.newRailLocation).add(0, 2, 0));
        excavate.add(new Vector3i(event.newRailLocation).add(perp).add(0, 2, 0));
        excavate.add(new Vector3i(event.newRailLocation).sub(perp).add(0, 2, 0));

        EntityRef boreDrill = cart.getComponent(InventoryComponent.class).itemSlots.get(3);
        Prefab damageType = boreDrill == EntityRef.NULL ? entityManager.create("engine:physicalDamage").getParentPrefab() : boreDrill.getComponent(BoreDrillComponent.class).damageType;

        Vector3i loc = excavate.get(rand.nextInt(excavate.size()));
        if (!worldProvider.getBlock(loc).equals(air) &&
            !blockEntityRegistry.getBlockEntityAt(loc).hasComponent(RailComponent.class)) {
            EntityRef blockEntity = blockEntityRegistry.getBlockEntityAt(loc);
            blockEntity.send(new DoDamageEvent(1, damageType, cart, boreDrill));
        }
    }

    @ReceiveEvent
    public void onFilterDrill(BeforeItemPutInInventory event, EntityRef cart, TunnelBoreCartComponent comp) {
        if (event.getSlot() == 3 && !event.getItem().hasComponent(BoreDrillComponent.class)) {
            event.consume();
        }
    }
}
