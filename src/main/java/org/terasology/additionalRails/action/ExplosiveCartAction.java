// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.additionalRails.action;

import org.terasology.additionalRails.components.ExplosiveCartComponent;
import org.terasology.additionalRails.events.CartActivatedEvent;
import org.terasology.additionalRails.ui.ExplosiveCartScreen;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.engine.logic.delay.DelayManager;
import org.terasology.engine.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.nui.NUIManager;
import org.terasology.explosives.logic.ExplosionActionComponent;

/**
 * System covering Explosive Cart's behavior.
 *
 * @author Aleksander Wójtowicz <anuar2k@outlook.com>
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class ExplosiveCartAction extends BaseComponentSystem {
    static final String CART_EXPLOSION_ACTION_ID = "CART_EXPLOSION";
    static final String SCREEN_URI = "AdditionalRails:ExplosiveCartScreen";

    @In
    DelayManager delayManager;
    @In
    NUIManager nuiManager;

    /**
     * Called when cart enters the Activator Rail. The "CART_EXPLOSION" DelayedAction is set for {@link
     * ExplosiveCartComponent} fuseLengthMs.
     *
     * @param event called by Activator Rail.
     * @param entity of the cart.
     * @param ecComponent carrying information how long is the fuse.
     */
    @ReceiveEvent(components = {ExplosiveCartComponent.class, LocationComponent.class})
    public void onActivateFuseOnCart(CartActivatedEvent event, EntityRef entity, ExplosiveCartComponent ecComponent) {
        delayManager.addDelayedAction(entity, CART_EXPLOSION_ACTION_ID, ecComponent.fuseLengthMs);
    }

    /**
     * Called when "CART_EXPLOSION" DelayedAction is triggered. The {@link ExplosionActionComponent} is added to the
     * cart's entity just before exploding, because every entity carrying it explodes on {@link ActivateEvent} (it's
     * defined in {@link ExplosionAuthoritySystem}. We don't want to make our cart do that. This is the reason why we
     * can't have the cart carrying {@link ExplosionActionComponent} and why we can't set "DELAYED_EXPLOSION" action
     * from the beginning.
     *
     * @param event called by {@link DelayManager}.
     * @param entity of the cart.
     * @param ecComponent differentiating Explosive Cart from others.
     */
    @ReceiveEvent(components = {ExplosiveCartComponent.class, LocationComponent.class})
    public void onFuseBurnt(DelayedActionTriggeredEvent event, EntityRef entity, ExplosiveCartComponent ecComponent) {
        if (event.getActionId().equals(CART_EXPLOSION_ACTION_ID)) {
            entity.addComponent(new ExplosionActionComponent());
            //DelayedActionTriggeredEvent is sent without the delay (what a paradox lol) to ka-boom-kachow the cart.
            entity.send(new DelayedActionTriggeredEvent("Delayed Explosion"/*ExplosionAuthoritySystem
            .DELAYED_EXPLOSION_ACTION_ID*/));
        }
    }

    /**
     * Opens up a UI window in which you can set the cart's fuse length.
     *
     * @param event called by the player.
     * @param entity of the cart.
     * @param ecComponent differentiating Explosive Cart from others.
     */
    @ReceiveEvent(components = {ExplosiveCartComponent.class, LocationComponent.class})
    public void onActivatedByPlayer(ActivateEvent event, EntityRef entity, ExplosiveCartComponent ecComponent) {
        nuiManager.toggleScreen(SCREEN_URI);
        ExplosiveCartScreen screen = (ExplosiveCartScreen) nuiManager.getScreen(SCREEN_URI);
        screen.attachToEntity(entity);
    }
}
