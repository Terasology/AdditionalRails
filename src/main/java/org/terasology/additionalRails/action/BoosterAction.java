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
import org.terasology.additionalRails.components.BoosterRailComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.math.geom.Vector3f;
import org.terasology.minecarts.blocks.RailComponent;
import org.terasology.minecarts.components.RailVehicleComponent;
import org.terasology.minecarts.controllers.CartMotionSystem;
import org.terasology.registry.In;
import org.terasology.segmentedpaths.components.PathFollowerComponent;
import org.terasology.segmentedpaths.events.OnExitSegment;
import org.terasology.segmentedpaths.events.OnVisitSegment;

import java.util.Set;

@RegisterSystem(RegisterMode.AUTHORITY)
public class BoosterAction extends BaseComponentSystem implements UpdateSubscriberSystem {
    private Set<EntityRef> segmentEntities = Sets.newHashSet();

    @In
    CartMotionSystem cartMotionSystem;
    public BoosterAction(){

    }

    @ReceiveEvent(components = {BoosterRailComponent.class, RailComponent.class})
    public void onEnterBoosterSegment(OnVisitSegment event, EntityRef entity) {
        segmentEntities.add(event.getSegmentEntity());
    }

    @ReceiveEvent(components = {BoosterRailComponent.class, RailComponent.class})
    public void onExitBoosterSegment(OnExitSegment event, EntityRef entity) {
        segmentEntities.remove(event.getSegmentEntity());
    }

    @Override
    public void update(float delta) {
        segmentEntities.removeIf(entityRef -> !entityRef.exists() || !entityRef.hasComponent(RailVehicleComponent.class) || !entityRef.hasComponent(PathFollowerComponent.class));
        for (EntityRef ref: segmentEntities)
        {
            RailVehicleComponent railVehicleComponent = ref.getComponent(RailVehicleComponent.class);
            if(railVehicleComponent.velocity.lengthSquared() < 25f) {
                Vector3f additionalVelocity = new Vector3f(railVehicleComponent.velocity).normalize().mul((20f / 2.0f) * delta);
                railVehicleComponent.velocity.add(additionalVelocity);
                ref.saveComponent(railVehicleComponent);
            }

        }
    }
}
