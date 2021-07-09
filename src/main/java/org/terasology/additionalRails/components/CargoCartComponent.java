
// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.additionalRails.components;

import org.terasology.gestalt.entitysystem.component.Component;

public class CargoCartComponent implements Component<CargoCartComponent> {
	public int weight = 0;

	@Override
	public void copy(CargoCartComponent other) {
		this.weight = other.weight;
	}
}
