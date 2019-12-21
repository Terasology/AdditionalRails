# AdditionalRails

AdditionalRails is an extension for the [Rails](https://github.com/Terasology/Rails) module which adds many new carts, rails, and other features.

## Carts
- Activable Cart Example: Prints a message to the logger when is passes over an Activator Rail
- Explosive Cart: Explodes when it passes over an Activator Rail
- Harvesting Cart: Harvests plants from the [SimpleFarming](https://github.com/Terasology/SimpleFarming) module
- Planting Cart: Plants plants from the [SimpleFarming](https://github.com/Terasology/SimpleFarming) module
- Hoover Cart: Picks up items around it when fueled by torches
- Track Layer Cart: Lays tracks from its inventory as it moves

## Rails
- Booster Rail: Boosts carts that pass over it
- One-way Booster Rail: Boosts carts in only one direction
- Activator Rail: Activates carts that pass over it
- Friction Rail: Has much more friction than other rails

## Switches
- Lever Switch: Controls the rail juntion direction, controlled by 'use target' action (usually 'E' key)
- Signal Switch: Same as the Lever Switch, but controlled by a signal from the [Signalling](https://github.com/Terasology/Signalling) module instead of the player
