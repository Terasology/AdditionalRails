// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.additionalRails.action;

import org.joml.Vector3f;
import org.terasology.additionalRails.components.CargoCartComponent;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.registry.In;
import org.terasology.gestalt.entitysystem.event.ReceiveEvent;
import org.terasology.minecarts.components.RailVehicleComponent;
import org.terasology.module.inventory.components.InventoryComponent;
import org.terasology.module.inventory.events.BeforeItemPutInInventory;

@RegisterSystem(RegisterMode.AUTHORITY)
public class CargoCartAction extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final int MAX_ITEMS = 2971;

    @In
    private EntityManager entityManager;

    @Override
    public void update(float delta) {
        for (EntityRef cargoCart : entityManager.getEntitiesWith(CargoCartComponent.class)) {
            CargoCartComponent cargoComponent = cargoCart.getComponent(CargoCartComponent.class);
            RailVehicleComponent vehicleComponent = cargoCart.getComponent(RailVehicleComponent.class);

            float mult = (MAX_ITEMS - cargoComponent.weight) / (float) MAX_ITEMS;

            Vector3f velocity = vehicleComponent.velocity;
            velocity = velocity.mul(mult);
            vehicleComponent.velocity = velocity;
            cargoCart.addOrSaveComponent(vehicleComponent);
        }
    }

    @ReceiveEvent
    public void onItemAdded(BeforeItemPutInInventory event, EntityRef entity, InventoryComponent inventory,
                            CargoCartComponent cargoComponent) {
        cargoComponent.weight = 0;
        for (EntityRef item : inventory.itemSlots) {
            if (item == EntityRef.NULL) {
                continue;
            }
            if (!item.hasComponent(ItemComponent.class)) {
                continue;
            }
            ItemComponent itemComponent = item.getComponent(ItemComponent.class);
            cargoComponent.weight += itemComponent.stackCount;
        }
    }
}
