// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.additionalRails.action;

import com.google.common.collect.Sets;
import org.joml.Vector3f;
import org.terasology.additionalRails.components.OnewayBoosterRailComponent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.prefab.Prefab;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.math.Rotation;
import org.terasology.engine.math.Side;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.minecarts.blocks.RailBlockFamily;
import org.terasology.minecarts.blocks.RailComponent;
import org.terasology.minecarts.components.RailVehicleComponent;
import org.terasology.segmentedpaths.components.BlockMappingComponent;
import org.terasology.segmentedpaths.components.PathFollowerComponent;
import org.terasology.segmentedpaths.events.OnExitSegment;
import org.terasology.segmentedpaths.events.OnVisitSegment;

import java.util.Objects;
import java.util.Set;

@RegisterSystem(RegisterMode.AUTHORITY)
public class OnewayBoosterAction extends BaseComponentSystem implements UpdateSubscriberSystem {
    private static final int PUSH_RATE = 20;
    private static final int VELOCITY_LENGTH_MAX = 25;

    private Set<RailCart> entities = Sets.newHashSet(); //The set of all pairs of a tile of OnewayBoosterRail and a
    // vehicle on it

    @In
    private BlockManager blockManager; //For retrieving the block family


    @ReceiveEvent(components = {OnewayBoosterRailComponent.class, RailComponent.class})
    public void onEnterBoosterSegment(OnVisitSegment event, EntityRef entity) { //entity is the rail and event
        // .getSegmentEntity() the cart
        entities.add(new RailCart(entity, event.getPathFollowingEntity()));
    }

    @ReceiveEvent(components = {OnewayBoosterRailComponent.class, RailComponent.class})
    public void onExitBoosterSegment(OnExitSegment event, EntityRef entity) {
        entities.remove(new RailCart(entity, event.getPathFollowingEntity()));
    }

    @Override
    public void update(float delta) {
        entities.removeIf(e -> !e.cart.exists() || !e.cart.hasComponent(RailVehicleComponent.class) || !e.cart.hasComponent(PathFollowerComponent.class)
                || !e.rail.exists() || !e.rail.hasComponent(OnewayBoosterRailComponent.class));
        for (RailCart rc : entities) {
            Block block = rc.rail.getComponent(BlockComponent.class).getBlock();
            RailBlockFamily family = (RailBlockFamily) block.getBlockFamily();
            //Get the direction of the rail
            Rotation rotation = family.getRotationFor(block.getURI());
            float yaw = rotation.getYaw().getRadians();
            Vector3f railDirection = new Vector3f((float) Math.cos(yaw), 0, (float) Math.sin(yaw)); //https
            // ://stackoverflow.com/a/1568687
            //Modify the direction
            //Also boost y-axis if the rail is a slope.
            Prefab prefab = rc.cart.getComponent(PathFollowerComponent.class).segmentMeta.prefab;
            BlockMappingComponent blockMappingComponent = prefab.getComponent(BlockMappingComponent.class);

            if (blockMappingComponent.s1 == Side.TOP || blockMappingComponent.s2 == Side.TOP) {
                railDirection.y += 1; //Every original slope rail points upward and inverted ones downward, so the y
                // value will always be correct after inverting below.
            }
            if (family == blockManager.getBlockFamily("AdditionalRails:OnewayBoosterRailInverted")) {
                railDirection.mul(-1);
            }
            //On z-axis, the direction the tile images point to is the opposite of the real direction they send carts
            // to.
            railDirection.z = -1;

            push(rc.cart, railDirection.mul(PUSH_RATE * delta));
        }
    }

    /**
     * Adds {@code thisMuch} to the velocity of {@code ref}'s {@code RailVehicleComponent} if it doesn't exceed {@link
     * #VELOCITY_LENGTH_MAX} or the addition decreases it.
     */
    private void push(EntityRef ref, Vector3f thisMuch) {
        RailVehicleComponent railVehicleComponent = ref.getComponent(RailVehicleComponent.class);
        Vector3f velocity = railVehicleComponent.velocity;
        //Allow pushing if the velocity decreases after the operation or doesn't exceed the maximum.
        if (velocity.lengthSquared() < VELOCITY_LENGTH_MAX || new Vector3f(velocity).add(thisMuch).lengthSquared() <= VELOCITY_LENGTH_MAX) {
            velocity.add(thisMuch);
            ref.saveComponent(railVehicleComponent);
        }
    }

    private class RailCart {
        EntityRef rail;
        EntityRef cart;

        RailCart(EntityRef rail, EntityRef cart) {
            this.rail = rail;
            this.cart = cart;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof RailCart) {
                RailCart other = (RailCart) obj;
                return this.rail == other.rail && this.cart == other.cart;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(rail, cart);
        }
    }
}
