package org.terasology.additionalRails.action;

import org.joml.Vector3f;
import org.terasology.additionalRails.components.HooverCartComponent;
import org.terasology.engine.entitySystem.Component;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.engine.logic.inventory.InventoryComponent;
import org.terasology.engine.logic.inventory.InventoryManager;
import org.terasology.engine.logic.inventory.ItemComponent;
import org.terasology.engine.logic.inventory.PickupComponent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.physics.Physics;
import org.terasology.engine.physics.StandardCollisionGroup;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.items.BlockItemComponent;
import org.terasology.joml.geom.AABBf;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * System covering Hoover Cart's behavior.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class HooverCartAction extends BaseComponentSystem implements UpdateSubscriberSystem {
    //our cart has 15-slot inventory, and we want to use only 14 of them - first one is fuel input slot.
    private static final Integer[] INV_SLOTS_ARR = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
    private static final List<Integer> INV_SLOTS = Arrays.asList(INV_SLOTS_ARR);

    @In
    EntityManager entityManager;
    @In
    InventoryManager inventoryManager;
    @In
    Physics physics;

    private boolean isTorch(BlockItemComponent blockItem) {
        return blockItem.blockFamily.getURI().toString().toLowerCase().endsWith("torch");
    }

    @Override
    public void update(float delta) {
        for (EntityRef entity : entityManager.getEntitiesWith(HooverCartComponent.class, InventoryComponent.class, LocationComponent.class)) {
            HooverCartComponent hcComponent = entity.getComponent(HooverCartComponent.class);

            //we need to take a look at the first slot to check if there is any fuel for the cart
            //currently, we use torches as our fuel xD needs to be replaced
            //TODO: Let the cart consume different typel of fuel sources, from a configurable list or something.
            InventoryComponent iComponent = entity.getComponent(InventoryComponent.class);
            EntityRef fuelSlot = iComponent.itemSlots.get(0);
            if (fuelSlot != EntityRef.NULL) {
                if (fuelSlot.hasComponent(BlockItemComponent.class) && fuelSlot.hasComponent(ItemComponent.class)) {
                    ItemComponent itemComponent = fuelSlot.getComponent(ItemComponent.class);
                    BlockItemComponent biComponent = fuelSlot.getComponent(BlockItemComponent.class);
                    //add 5 energy units for 1 torch
                    if (isTorch(biComponent)) {
                        while (itemComponent.stackCount > 0) {
                            itemComponent.stackCount--;
                            hcComponent.energy += 5;
                        }
                        //clear the slot
                        iComponent.itemSlots.set(0, EntityRef.NULL);
                    }
                }
            }

            //if the cart has no energy - don't do anything
            if (hcComponent.energy <= 0) {
                return;
            }

            //create an AABB in which we will look for all entities
            LocationComponent lComponent = entity.getComponent(LocationComponent.class);
            Vector3f pos = lComponent.getWorldPosition(new Vector3f());
            AABBf scannedArea = new AABBf(pos,pos).expand(new Vector3f(1.5f, 0.5f, 1.5f));

            //remove non-pickable entities from the list
            List<EntityRef> foundEntities = physics.scanArea(scannedArea, StandardCollisionGroup.ALL);
            ListIterator<EntityRef> iter = foundEntities.listIterator();
            while (iter.hasNext()) {
                if (!iter.next().hasComponent(PickupComponent.class)) {
                    iter.remove();
                }
            }

            //if we've found any pickable entities
            if (foundEntities.size() > 0) {
                iter = foundEntities.listIterator();
                while (hcComponent.energy > 0 && iter.hasNext()) {
                    EntityRef item = iter.next();
                    ItemComponent itemComponent = item.getComponent(ItemComponent.class);

                    //does literally the same what ItemPickupAuthoritySystem does.
                    if (inventoryManager.giveItem(entity, entity, item, INV_SLOTS)) {
                        if (itemComponent != null) {
                            for (Component c : itemComponent.pickupPrefab.iterateComponents()) {
                                item.removeComponent(c.getClass());
                            }
                        }
                        hcComponent.energy--;
                    } else {
                        break;
                    }
                }
            }
        }
    }
}
