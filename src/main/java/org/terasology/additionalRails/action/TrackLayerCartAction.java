// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.additionalRails.action;

import org.joml.Vector3i;
import org.terasology.additionalRails.components.TrackLayerCartComponent;
import org.terasology.additionalRails.events.LayTrackEvent;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.math.Side;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.family.BlockFamily;
import org.terasology.engine.world.block.family.BlockPlacementData;
import org.terasology.engine.world.block.items.BlockItemComponent;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.minecarts.blocks.RailBlockFamily;
import org.terasology.minecarts.blocks.RailComponent;
import org.terasology.minecarts.components.RailVehicleComponent;
import org.terasology.module.inventory.components.InventoryComponent;
import org.terasology.segmentedpaths.components.PathFollowerComponent;

import java.util.List;

/**
 * System covering Track Layer Cart's behavior.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class TrackLayerCartAction extends BaseComponentSystem implements UpdateSubscriberSystem {
    @In
    private EntityManager entityManager;
    @In
    private WorldProvider worldProvider;

    @Override
    public void update(float delta) {
        //Look for all Track Layer Carts.
        for (EntityRef entity : entityManager.getEntitiesWith(TrackLayerCartComponent.class, RailVehicleComponent.class,
                PathFollowerComponent.class, InventoryComponent.class)) {
            //Get the rail block under the cart.
            PathFollowerComponent pfComp = entity.getComponent(PathFollowerComponent.class);
            EntityRef rbEntity = pfComp.segmentMeta.association;

            if (!rbEntity.hasComponent(RailComponent.class)) {
                continue;
            }

            BlockComponent bComp = rbEntity.getComponent(BlockComponent.class);
            Vector3i rbLocation = new Vector3i(bComp.getPosition(new Vector3i()));
            Block rBlock = bComp.getBlock();

            Vector3i heading = new Vector3i(pfComp.heading, org.joml.RoundingMode.CEILING);

            //Only work with straight tracks
            if (heading.x() != 0 && heading.y() != 0) {
                continue;
            }

            Side side = Side.inDirection(heading.x(), 0, heading.z());

            RailBlockFamily ruFamily = (RailBlockFamily) rBlock.getBlockFamily();
            Vector3i newRailLocation = new Vector3i(rbLocation).add(side.direction());
            entity.send(new LayTrackEvent(newRailLocation));
            side = side.reverse();
            newRailLocation = new Vector3i(rbLocation).add(side.direction());
            entity.send(new LayTrackEvent(newRailLocation));
        }
    }

    @ReceiveEvent
    public void onLayTrack(LayTrackEvent event, EntityRef cart, TrackLayerCartComponent comp) {
        Vector3i newRailLocation = event.newRailLocation;

        //If the there is no space for new rail - go to another cart entity.
        Block nextBlock = worldProvider.getBlock(newRailLocation);
        if (!nextBlock.getURI().equals(BlockManager.AIR_ID)) {
            return;
        }

        //We have to do the same if there is no block on which rail would lay on.
        Block underNextBlock = worldProvider.getBlock(new Vector3i(newRailLocation).add(new Vector3i(0, -1, 0)));
        if (underNextBlock.isPenetrable() || underNextBlock.isLiquid()) {
            return;
        }

        //Now we're checking if rail's inventory has some rails to be placed.
        InventoryComponent iComponent = cart.getComponent(InventoryComponent.class);
        List<EntityRef> slots = iComponent.itemSlots;

        BlockFamily ruFamily = null;
        boolean gotItem = false;
        for (EntityRef slot : slots) {
            //If it's not a rail block - go to next slot.
            if (slot == EntityRef.NULL) {
                continue;
            }
            if (!slot.hasComponent(ItemComponent.class)) {
                continue;
            }
            if (!slot.hasComponent(BlockItemComponent.class)) {
                continue;
            }
            ItemComponent item = slot.getComponent(ItemComponent.class);
            BlockItemComponent bitem = slot.getComponent(BlockItemComponent.class);
            //If the block item is a rail block...
            if (bitem.blockFamily instanceof RailBlockFamily) {
                ruFamily = bitem.blockFamily;
                item.stackCount--;
                gotItem = true;
                //If the stack's count is equal or lower than zero, remove the stack from inventory.
                if (item.stackCount <= 0) {
                    iComponent.itemSlots.set(iComponent.itemSlots.indexOf(slot), EntityRef.NULL);
                }
                break;
            }
        }

        //Place the new rail if it was available in the inventory.
        if (gotItem) {
            Block rBlock = ruFamily.getBlockForPlacement(new BlockPlacementData(newRailLocation, null, null));
            worldProvider.setBlock(newRailLocation, rBlock);
        }
    }
}
