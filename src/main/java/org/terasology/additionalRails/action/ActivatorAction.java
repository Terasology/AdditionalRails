package org.terasology.additionalRails.action;

import org.terasology.additionalRails.components.ActivatorRailComponent;
import org.terasology.additionalRails.events.CartActivatedEvent;
import org.terasology.additionalRails.events.CartDeactivatedEvent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.minecarts.blocks.RailComponent;
import org.terasology.segmentedpaths.events.OnExitSegment;
import org.terasology.segmentedpaths.events.OnVisitSegment;

/**
 * System, which covers the Activator Rail behavior. The {@link org.terasology.additionalRails.events.CartActivatedEvent} is called to every cart, which enters the Activator Rail.
 * {@link org.terasology.additionalRails.events.CartDeactivatedEvent} is called, when a cart leaves the rail.
 * @author Aleksander Wójtowicz <anuar2k@outlook.com>
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class ActivatorAction extends BaseComponentSystem {

    /**
     * We're listening for {@link org.terasology.segmentedpaths.events.OnVisitSegment} event, which is called when a cart enters the rail.
     * Next, the {@link org.terasology.additionalRails.events.CartActivatedEvent} is called on the entity which entered the rail.
     * @param event OnVisitSegment event called by {@link org.terasology.segmentedpaths.controllers.SegmentSystem}.
     * @param entity reference pointing to the rail, which called the event. Not used.
     */
    @ReceiveEvent(components = {ActivatorRailComponent.class, RailComponent.class})
    public void onEnterActivatorSegment(OnVisitSegment event, EntityRef entity) {
        EntityRef segmentEntity = event.getSegmentEntity();
        segmentEntity.send(new CartActivatedEvent());
    }

    /**
     * We're listening for {@link org.terasology.segmentedpaths.events.OnExitSegment} event, which is called when a cart leaves the rail.
     * Next, the {@link org.terasology.additionalRails.events.CartDeactivatedEvent} is called on the entity which left the rail.
     * @param event OnExitSegment event called by {@link org.terasology.segmentedpaths.controllers.SegmentSystem}.
     * @param entity reference pointing to the rail, which called the event. Not used.
     */
    @ReceiveEvent(components = {ActivatorRailComponent.class, RailComponent.class})
    public void onExitActivatorSegment(OnExitSegment event, EntityRef entity) {
        EntityRef segmentEntity = event.getSegmentEntity();
        segmentEntity.send(new CartDeactivatedEvent());
    }
}
