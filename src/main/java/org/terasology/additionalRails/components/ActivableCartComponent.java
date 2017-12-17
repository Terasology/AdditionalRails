package org.terasology.additionalRails.components;

import org.terasology.entitySystem.Component;

/**
 * Component which makes the cart able to receive {@link org.terasology.additionalRails.events.CartActivatedEvent}.
 * Every cart, which want to use the Activator Rail, must contain this component.
 * @author Aleksander Wójtowicz <anuar2k@outlook.com>
 */
public class ActivableCartComponent implements Component {
    /**
     * eventCalled param is used to mark, if there was an event called to the cart already (it also defines whether the cart is above Activator Rail or not).
     */
    public boolean eventCalled = false;
}
