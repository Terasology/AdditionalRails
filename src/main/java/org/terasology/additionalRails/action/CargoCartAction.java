package org.terasology.additionalRails.action;

import org.terasology.additionalRails.components.CargoCartComponent;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.minecarts.components.RailVehicleComponent;
import org.terasology.registry.In;

@RegisterSystem(RegisterMode.AUTHORITY)
public class CargoCartAction extends BaseComponentSystem implements UpdateSubscriberSystem {
    
    private static final int MAX_ITEMS = 2971;
    
    @In
    private EntityManager entityManager;
    
    @Override
    public void update(float delta) {
        for(EntityRef cargoCart : entityManager.getEntitiesWith(CargoCartComponent.class)) {
            RailVehicleComponent vehicleComponent = cargoCart.getComponent(RailVehicleComponent.class);
            InventoryComponent inventory = cargoCart.getComponent(InventoryComponent.class);
            int numItems = 0;
            for(EntityRef item : inventory.itemSlots) {
                if (item == EntityRef.NULL) {
                    continue;
                }
                if (!item.hasComponent(ItemComponent.class)) {
                    continue;
                }
                ItemComponent itemComponent = item.getComponent(ItemComponent.class);
                numItems += itemComponent.stackCount;
            }
            
            float mult = (MAX_ITEMS - numItems)/(float) MAX_ITEMS;
            
            Vector3f velocity = vehicleComponent.velocity;
            if(numItems != 0) {
                velocity = velocity.mul(mult);
            }
            vehicleComponent.velocity = velocity;
            cargoCart.addOrSaveComponent(vehicleComponent);
        }
    }
}
