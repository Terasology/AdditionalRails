// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.additionalRails.components;

import org.terasology.engine.entitySystem.Component;

/**
 * Component differentiating Explosive Cart.
 *
 * @author Aleksander Wójtowicz <anuar2k@outlook.com>
 */
public class ExplosiveCartComponent implements Component {
    /**
     * Defines how long does it take to make the cart explode after being activated by Activator Rail.
     */
    public long fuseLengthMs = 4000;
}
