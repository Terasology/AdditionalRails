// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.additionalRails.ui;

import org.terasology.additionalRails.components.ExplosiveCartComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.rendering.nui.CoreScreenLayer;
import org.terasology.nui.widgets.UIButton;
import org.terasology.nui.widgets.UISlider;

/**
 * Explosive Cart's fuse length configurator's backend.
 * @author Aleksander Wójtowicz <anuar2k@outlook.com>
 */
public class ExplosiveCartScreen extends CoreScreenLayer {
    private EntityRef cartEntity;

    private UISlider slider;
    private UIButton setBtn;
    private UIButton cancelBtn;

    /**
     * UI Window setup.
     */
    @Override
    public void initialise() {
        slider = find("fuseSlider", UISlider.class);
        setBtn = find("setBtn", UIButton.class);
        cancelBtn = find("cancelBtn", UIButton.class);

        if (setBtn != null) {
            setBtn.subscribe(button -> {
                if (cartEntity.hasComponent(ExplosiveCartComponent.class)) {
                    if (slider != null) {
                        ExplosiveCartComponent ecComponent = cartEntity.getComponent(ExplosiveCartComponent.class);
                        ecComponent.fuseLengthMs = Math.round(slider.getValue()*1000);
                    }
                }
                triggerBackAnimation();
            });
        }

        if (cancelBtn != null) {
            cancelBtn.subscribe(button -> triggerBackAnimation());
        }
    }

    /**
     * Called everytime in {@link org.terasology.additionalRails.action.ExplosiveCartAction} when the window gets opened.
     * @param cartEntity of the Explosive Cart.
     */
    public void attachToEntity(EntityRef cartEntity) {
        this.cartEntity = cartEntity;
        if (cartEntity.hasComponent(ExplosiveCartComponent.class)) {
            if (slider != null) {
                ExplosiveCartComponent ecComponent = cartEntity.getComponent(ExplosiveCartComponent.class);
                slider.setValue(ecComponent.fuseLengthMs / 1000f);
            }
        }

    }
}
