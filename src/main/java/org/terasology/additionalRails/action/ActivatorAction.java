package org.terasology.additionalRails.action;

import org.terasology.additionalRails.components.ActivableCartComponent;
import org.terasology.additionalRails.components.ActivatorRailComponent;
import org.terasology.additionalRails.events.CartActivatedEvent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.minecarts.blocks.RailComponent;
import org.terasology.segmentedpaths.events.OnExitSegment;
import org.terasology.segmentedpaths.events.OnVisitSegment;

/**
 * System, which covers the Activator Rail behavior. The {@link org.terasology.additionalRails.events.CartActivatedEvent} is called, when a cart enters the rail for the 1st time.
 * Every cart using this system must contain {@link org.terasology.additionalRails.components.ActivableCartComponent}.
 * @author Aleksander Wójtowicz <anuar2k@outlook.com>
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class ActivatorAction extends BaseComponentSystem {

    /**
     * We're listening for OnVisitSegment event, which is called when a cart enters the rail.
     * If the cart has {@link org.terasology.additionalRails.components.ActivableCartComponent},
     * the {@link org.terasology.additionalRails.events.CartActivatedEvent} is called, which contains EntityRef pointing to the cart, which entered the rail.
     * We also set the component's eventCalled to true, to make sure that no more {@link org.terasology.additionalRails.events.CartActivatedEvent} is called to the cart.
     * The eventCalled param may be used later, for example to check if the cart is still on the Activator Rail.
     * @param event OnVisitSegment event called by {@link org.terasology.segmentedpaths.controllers.SegmentSystem}.
     * @param entity reference pointing to the rail, which called the event. Not used.
     */
    @ReceiveEvent(components = {ActivatorRailComponent.class, RailComponent.class})
    public void onEnterActivatorSegment(OnVisitSegment event, EntityRef entity) {
        EntityRef segmentEntity = event.getSegmentEntity();
        if (segmentEntity.hasComponent(ActivableCartComponent.class)) {
            ActivableCartComponent acComponent = segmentEntity.getComponent(ActivableCartComponent.class);
            if (!acComponent.eventCalled) {
                acComponent.eventCalled = true;
                segmentEntity.send(new CartActivatedEvent());
                segmentEntity.saveComponent(acComponent);
            }
        }
    }

    /**
     * We're listening for OnExitSegment event, which is called when a cart leaves the rail.
     * If the cart has {@link org.terasology.additionalRails.components.ActivableCartComponent},
     * we set the component's eventCalled to false, to make the cart be available to receive {@link org.terasology.additionalRails.events.CartActivatedEvent} events.
     * @param event OnExitSegment event called by {@link org.terasology.segmentedpaths.controllers.SegmentSystem}.
     * @param entity reference pointing to the rail, which called the event. Not used.
     */
    @ReceiveEvent(components = {ActivatorRailComponent.class, RailComponent.class})
    public void onExitActivatorSegment(OnExitSegment event, EntityRef entity) {
        EntityRef segmentEntity = event.getSegmentEntity();
        if (segmentEntity.hasComponent(ActivableCartComponent.class)) {
            ActivableCartComponent acComponent = segmentEntity.getComponent(ActivableCartComponent.class);
            acComponent.eventCalled = false;
            segmentEntity.saveComponent(acComponent);
        }
    }
}
