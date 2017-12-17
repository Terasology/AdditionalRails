package org.terasology.additionalRails.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.additionalRails.components.ActivableCartComponent;
import org.terasology.additionalRails.events.CartActivatedEvent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;

/**
 * Example system utilising {@link org.terasology.additionalRails.events.CartActivatedEvent}.
 * @author Aleksander Wójtowicz <anuar2k@outlook.com>
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class ExampleActivableCartAction extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(ExampleActivableCartAction.class);

    /**
     * Example method retrieving the {@link org.terasology.additionalRails.events.CartActivatedEvent}.
     * This example should also check what type that cart is (e.g. TNT Cart).
     * @param event called by {@link org.terasology.additionalRails.action.ActivatorAction}.
     * @param entity (cart) which called the event.
     */
    @ReceiveEvent()
    public void cartActivatedEvent(CartActivatedEvent event, EntityRef entity) {
        if (entity.hasComponent(ActivableCartComponent.class)) { //we should look for the cart type right here
            logger.info("Activated cart id: {}", entity.getId());
        }
    }

}
