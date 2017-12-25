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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@RegisterSystem(RegisterMode.AUTHORITY)
public class OnewayBoosterAction extends BaseComponentSystem implements UpdateSubscriberSystem {
    private Set<EntityRef> carts = Sets.newHashSet();
    private Map<EntityRef, EntityRef> rails = Maps.newHashMap();

    private final static Logger logger = LoggerFactory.getLogger(BoosterAction.class);

    @ReceiveEvent(components = {OnewayBoosterRailComponent.class, RailComponent.class})
    public void onEnterBoosterSegment(OnVisitSegment event, EntityRef entity) { //event.getSegmentEntity() is the rail and entity the cart
        logger.warn("segmentEntity : " + event.getSegmentEntity().toString());
        logger.warn("entity : " + entity.toString());
        carts.add(entity);
        rails.put(entity, event.getSegmentEntity());
    }

    @ReceiveEvent(components = {OnewayBoosterRailComponent.class, RailComponent.class})
    public void onExitBoosterSegment(OnExitSegment event, EntityRef entity) {
        carts.remove(entity);
        rails.remove(entity);
    }

    @Override
    public void update(float delta) {
        for(Iterator<EntityRef> itr = carts.iterator();itr.hasNext();) {
            EntityRef cart = itr.next();
            EntityRef rail = rails.get(cart);
            if(!rail.exists() || !rail.hasComponent(RailVehicleComponent.class) || !rail.hasComponent(PathFollowerComponent.class)
                    || !cart.exists() || !cart.hasComponent(OnewayBoosterRailComponent.class)) {
                rails.remove(cart);
                itr.remove();
                continue;
            }
            accelerate(rail, (20f / 2.0f) * delta);
        }
    }
    private void accelerate(EntityRef ref, float multiplier) {
        RailVehicleComponent railVehicleComponent = ref.getComponent(RailVehicleComponent.class);
        if(railVehicleComponent.velocity.lengthSquared() < 25f) {
            Vector3f additionalVelocity = new Vector3f(railVehicleComponent.velocity).normalize().mul(multiplier);
            railVehicleComponent.velocity.add(additionalVelocity);
            ref.saveComponent(railVehicleComponent);
        }
    }
}