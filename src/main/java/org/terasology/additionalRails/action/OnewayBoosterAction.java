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

import com.google.common.collect.Sets;
import org.terasology.additionalRails.components.OnewayBoosterRailComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.math.geom.Vector3f;
import org.terasology.minecarts.blocks.RailComponent;
import org.terasology.minecarts.components.RailVehicleComponent;
import org.terasology.segmentedpaths.components.PathFollowerComponent;
import org.terasology.segmentedpaths.events.OnExitSegment;
import org.terasology.segmentedpaths.events.OnVisitSegment;

import java.util.Objects;
import java.util.Set;

@RegisterSystem(RegisterMode.AUTHORITY)
public class OnewayBoosterAction extends BaseComponentSystem implements UpdateSubscriberSystem {
    private Set<RailCart> entities = Sets.newHashSet();
    private static final int ACCELERATION_RATE = 10;

    @ReceiveEvent(components = {OnewayBoosterRailComponent.class, RailComponent.class})
    public void onEnterBoosterSegment(OnVisitSegment event, EntityRef entity) { //entity is the rail and event.getSegmentEntity() the cart
        entities.add(new RailCart(entity, event.getSegmentEntity()));
    }

    @ReceiveEvent(components = {OnewayBoosterRailComponent.class, RailComponent.class})
    public void onExitBoosterSegment(OnExitSegment event, EntityRef entity) {
        entities.remove(new RailCart(entity, event.getSegmentEntity()));
    }

    @Override
    public void update(float delta) {
        entities.removeIf(e -> !e.cart.exists() || !e.cart.hasComponent(RailVehicleComponent.class) || !e.cart.hasComponent(PathFollowerComponent.class)
                || !e.rail.exists() || !e.rail.hasComponent(OnewayBoosterRailComponent.class));
        for(RailCart rc : entities) {
            accelerate(rc.cart, delta);
        }
    }
    /**Adds {@link OnewayBoosterAction#ACCELERATION_RATE} per delta to x or z of the velocity of {@code ref}'s {@code RailVehicleComponent}*/
    private void accelerate(EntityRef ref, float delta) {
        RailVehicleComponent railVehicleComponent = ref.getComponent(RailVehicleComponent.class);
        Vector3f velocity = railVehicleComponent.velocity, accel;
        boolean isGoingOpposite;
        if(Math.abs(velocity.x) > Math.abs(velocity.z)) {
            accel = new Vector3f(ACCELERATION_RATE * delta, 0, 0);
            isGoingOpposite = (accel.x < 0) != (velocity.x < 0);
        } else {
            accel = new Vector3f(0, 0, ACCELERATION_RATE * delta);
            isGoingOpposite = (accel.z < 0) != (velocity.z < 0);
        }
        if(velocity.lengthSquared() < 25f || isGoingOpposite) {
            velocity.add(accel);
            ref.saveComponent(railVehicleComponent);
        }
    }
    private class RailCart {
        EntityRef rail, cart;
        RailCart(EntityRef rail, EntityRef cart) {
            this.rail = rail;
            this.cart = cart;
        }
        @Override
        public boolean equals(Object obj) {
            if(obj instanceof RailCart) {
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
    /*private void printDebug(EntityRef rail, EntityRef cart) {
    LocationComponent cartL = cart.getComponent(LocationComponent.class);
    RailVehicleComponent cartV = cart.getComponent(RailVehicleComponent.class);
    PathFollowerComponent cartP = cart.getComponent(PathFollowerComponent.class);
    LocationComponent railL = rail.getComponent(LocationComponent.class);
    logger.warn("cart : ");
    logger.warn("world position : " + cartL.getWorldPosition() + ", local position : " + cartL.getLocalPosition());
    logger.warn("local rotation : " + cartL.getLocalRotation() + ", world rotation : " + cartL.getWorldRotation());
    logger.warn("heading : " + cartP.heading);
    logger.warn("velocity : " + cartV.velocity);
    logger.warn("rail : ");
    logger.warn("world position : " + railL.getWorldPosition() + ", local position : " + railL.getLocalPosition());
    logger.warn("local rotation : " + railL.getLocalRotation() + ", world rotation : " + railL.getWorldRotation());
    }*/
}