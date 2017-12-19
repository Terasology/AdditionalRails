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

import org.terasology.additionalRails.components.LocomotiveComponent;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.math.geom.Vector3f;
import org.terasology.minecarts.components.RailVehicleComponent;
import org.terasology.minecarts.controllers.CartMotionSystem;
import org.terasology.registry.In;
import org.terasology.segmentedpaths.components.PathFollowerComponent;

@RegisterSystem(RegisterMode.AUTHORITY)
public class LocomotiveAction extends BaseComponentSystem implements UpdateSubscriberSystem {
    @In
    EntityManager entityManager;

    @In
    CartMotionSystem cartMotionSystem;

    @Override
    public void update(float delta) {
        for (EntityRef locomotiveVehicle: entityManager.getEntitiesWith(RailVehicleComponent.class, LocomotiveComponent.class, PathFollowerComponent.class)) {
            LocomotiveComponent locomotiveComponent = locomotiveVehicle.getComponent(LocomotiveComponent.class);
            RailVehicleComponent railVehicleComponent = locomotiveVehicle.getComponent(RailVehicleComponent.class);
            PathFollowerComponent segmentEntityComponent = locomotiveVehicle.getComponent(PathFollowerComponent.class);

            if(locomotiveComponent.active && railVehicleComponent.velocity.lengthSquared() < 10f) {
                Vector3f additionalVelocity = new Vector3f(segmentEntityComponent.heading).normalize().mul((20f / 2.0f) * delta);
                railVehicleComponent.velocity.add(additionalVelocity);
                locomotiveVehicle.saveComponent(railVehicleComponent);
            }

        }
    }

    @ReceiveEvent(components = {RailVehicleComponent.class, LocomotiveComponent.class})
    public void onUseFunctional(ActivateEvent event, EntityRef railVehicleEntity) {
        LocomotiveComponent locomotiveComponent = railVehicleEntity.getComponent(LocomotiveComponent.class);
        locomotiveComponent.active = !locomotiveComponent.active;
        railVehicleEntity.saveComponent(locomotiveComponent);
    }
}
