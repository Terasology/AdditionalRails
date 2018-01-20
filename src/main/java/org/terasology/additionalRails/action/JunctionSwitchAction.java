package org.terasology.additionalRails.action;

import org.terasology.additionalRails.block.TwoStateFamily;
import org.terasology.additionalRails.components.JunctionSwitchComponent;
import org.terasology.additionalRails.components.JunctionSwitchSignalledComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.math.SideBitFlag;
import org.terasology.minecarts.blocks.RailComponent;
import org.terasology.minecarts.blocks.RailsUpdateFamily;
import org.terasology.registry.In;
import org.terasology.signalling.components.SignalConsumerStatusComponent;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.items.OnBlockItemPlaced;

/**
 * Class describing behavior of Junction Switches.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class JunctionSwitchAction extends BaseComponentSystem {

    @In
    private WorldProvider worldProvider;
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private BlockManager blockManager;

    private RailsUpdateFamily railFamily;
    private RailsUpdateFamily invertFamily;

    /**
     * Prepares blocks for future use (see code below)
     * These block are obtained from an instance of {@link org.terasology.world.block.BlockManager}
     */
    @Override
    public void initialise() {
        railFamily = (RailsUpdateFamily)blockManager.getBlockFamily("Rails:rails");
        invertFamily = (RailsUpdateFamily)blockManager.getBlockFamily("Rails:railsTBlockInverted");
    }

    /**
     * Updates a Junction Switch and rails around it, when user interacts with the block.
     * @param event ActivateEvent called upon activation
     * @param entity The entity of a Junction Switch
     */
    @ReceiveEvent(components = {BlockComponent.class, JunctionSwitchComponent.class})
    public void junctionSwitchAction(ActivateEvent event, EntityRef entity, BlockComponent blockComponent) {
        Block switchBlock = blockComponent.getBlock();
        TwoStateFamily family = (TwoStateFamily)switchBlock.getBlockFamily();

        Boolean state = family.getState(switchBlock);
        state = !state;
        switchBlock = family.getBlockForState(state);

        Vector3i position = blockComponent.getPosition();
        blockEntityRegistry.setBlockForceUpdateEntity(position, switchBlock);

        updateRails(position, state);
    }

    /**
     * Updates Signal-controlled Junction Switch and rails around it, when {@link org.terasology.signalling} signal changes its state.
     * @param event OnChangedComponent event raised by modified {@link org.terasology.signalling.components.SignalConsumerStatusComponent}.
     * @param entity The entity of a Signal-controlled Junction Switch.
     */
    @ReceiveEvent(components = {BlockComponent.class, JunctionSwitchSignalledComponent.class, SignalConsumerStatusComponent.class})
    public void railSwitchSignalAction(OnChangedComponent event, EntityRef entity, BlockComponent blockComponent, SignalConsumerStatusComponent scsComponent) {
        Block switchBlock = blockComponent.getBlock();
        TwoStateFamily family = (TwoStateFamily)switchBlock.getBlockFamily();

        switchBlock = family.getBlockForState(scsComponent.hasSignal);

        Vector3i position = blockComponent.getPosition();
        blockEntityRegistry.setBlockRetainComponent(position, switchBlock, SignalConsumerStatusComponent.class);

        updateRails(position, scsComponent.hasSignal);
    }

    @ReceiveEvent
    public void onSwitchPlaced(OnBlockItemPlaced event, EntityRef entity) {
        EntityRef placedBlock = event.getPlacedBlock();
        JunctionSwitchComponent jsComponent = placedBlock.getComponent(JunctionSwitchComponent.class);
        JunctionSwitchSignalledComponent jssComponent = placedBlock.getComponent(JunctionSwitchSignalledComponent.class);
        if (jsComponent != null || jssComponent != null) {
            updateRails(event.getPosition(), false);
        }
    }

    /**
     * Looks for rails around switchLocation and updates them basing on inverted bool
     * @param switchLocation location of the switch
     * @param inverted boolean which is false if the rail juntion should be normal, or true, if the rail juntion should be inverted
     */
    private void updateRails(Vector3i switchLocation, boolean inverted) {
        //prepare the locations around the switch, where we're going to look for rails
        Vector3i[] railLocations = {new Vector3i(switchLocation).add(Vector3i.north()),
                                    new Vector3i(switchLocation).add(Vector3i.east()),
                                    new Vector3i(switchLocation).add(Vector3i.south()),
                                    new Vector3i(switchLocation).add(Vector3i.west()),
                                    new Vector3i(switchLocation).add(Vector3i.up())};

        //iterate through all the positions and change the rail's state if needed
        for (Vector3i railLocation : railLocations) {
            Block block = worldProvider.getBlock(railLocation);
            EntityRef entity = block.getEntity();

            if (!entity.hasComponent(RailComponent.class)) {
                continue; //if the block is not a rail, go to the next iteration of the loop
            }

            //TODO: Find out a way to make this work with all rail types, not only the standard one.
            byte connections = Byte.parseByte(block.getURI().getIdentifier().toString());

            if (SideBitFlag.getSides(connections).size() == 3) {
                if (block.getBlockFamily() == railFamily || block.getBlockFamily() == invertFamily) {
                    blockEntityRegistry.setBlockForceUpdateEntity(railLocation, inverted ? invertFamily.getBlockByConnection(connections) : railFamily.getBlockByConnection(connections));
                }
            }
        }
    }
}
