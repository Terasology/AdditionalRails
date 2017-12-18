package org.terasology.additionalRails.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.additionalRails.components.ExampleActivableCartComponent;
import org.terasology.additionalRails.events.CartActivatedEvent;
import org.terasology.additionalRails.events.CartDeactivatedEvent;
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
     * Example way of checking, if a cart of our desired type was activated (entered the rail).
     * @param event which was called by {@link org.terasology.additionalRails.action.ActivatorAction}
     * @param entity of the cart.
     */
    @ReceiveEvent(components = {ExampleActivableCartComponent.class})
    public void cartActivatedEvent(CartActivatedEvent event, EntityRef entity) {
        logger.info("Activated Example Activable Cart ID: {}", entity.getId());
    }

    /**
     * Example way of checking, if a cart of our desired type was deactivated (left the rail).
     * @param event which was called by {@link org.terasology.additionalRails.action.ActivatorAction}
     * @param entity of the cart.
     */
    @ReceiveEvent(components = {ExampleActivableCartComponent.class})
    public void cartDeactivatedEvent(CartDeactivatedEvent event, EntityRef entity) {
        logger.info("Deactivated Example Activable Cart ID: {}", entity.getId());
    }

}
