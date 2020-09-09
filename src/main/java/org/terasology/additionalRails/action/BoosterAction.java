// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.additionalRails.action;

import com.google.common.collect.Sets;
import org.terasology.additionalRails.components.BoosterRailComponent;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.math.geom.Vector3f;
import org.terasology.minecarts.blocks.RailComponent;
import org.terasology.minecarts.components.RailVehicleComponent;
import org.terasology.segmentedpaths.components.PathFollowerComponent;
import org.terasology.segmentedpaths.events.OnExitSegment;
import org.terasology.segmentedpaths.events.OnVisitSegment;

import java.util.Set;

@RegisterSystem(RegisterMode.AUTHORITY)
public class BoosterAction extends BaseComponentSystem implements UpdateSubscriberSystem {
    private final Set<EntityRef> segmentEntities = Sets.newHashSet();

    public BoosterAction() {

    }

    @ReceiveEvent(components = {BoosterRailComponent.class, RailComponent.class})
    public void onEnterBoosterSegment(OnVisitSegment event, EntityRef entity) {
        segmentEntities.add(event.getPathFollowingEntity());
    }

    @ReceiveEvent(components = {BoosterRailComponent.class, RailComponent.class})
    public void onExitBoosterSegment(OnExitSegment event, EntityRef entity) {
        segmentEntities.remove(event.getPathFollowingEntity());
    }

    @Override
    public void update(float delta) {
        segmentEntities.removeIf(entityRef -> !entityRef.exists() || !entityRef.hasComponent(RailVehicleComponent.class) || !entityRef.hasComponent(PathFollowerComponent.class));
        for (EntityRef ref : segmentEntities) {
            RailVehicleComponent railVehicleComponent = ref.getComponent(RailVehicleComponent.class);
            if (railVehicleComponent.velocity.lengthSquared() < 25f) {
                Vector3f additionalVelocity =
                        new Vector3f(railVehicleComponent.velocity).normalize().mul((20f / 2.0f) * delta);
                railVehicleComponent.velocity.add(additionalVelocity);
                ref.saveComponent(railVehicleComponent);
            }
        }
    }
}
