package org.terasology.additionalRails.action;

import org.terasology.additionalRails.components.RailSwitchLeverComponent;
import org.terasology.additionalRails.components.RailSwitchSignalComponent;
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

/**
 * Class describing railSwitch's behavior.
 * @author Aleksander Wójtowicz <anuar2k@outlook.com>
 * Code below is inspired by Marcin Sciesinki's Signalling module and michaelpollind's Rails module
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class RailSwitchAction extends BaseComponentSystem {

    @In
    private WorldProvider worldProvider;
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private BlockManager blockManager;

    private Block railSwitchLeverOff;
    private Block railSwitchLeverOn;
    private Block railSwitchSignalOff;
    private Block railSwitchSignalOn;
    private RailsUpdateFamily railFamily;
    private RailsUpdateFamily invertFamily;

    /**
     * Prepares blocks for future use (see code below)
     * These block are obtained from an instance of {@link org.terasology.world.block.BlockManager}
     */
    @Override
    public void initialise() {
        railSwitchLeverOff = blockManager.getBlock("AdditionalRails:railSwitchLeverOff");
        railSwitchLeverOn = blockManager.getBlock("AdditionalRails:railSwitchLeverOn");
        railSwitchSignalOff = blockManager.getBlock("AdditionalRails:railSwitchSignalOff");
        railSwitchSignalOn = blockManager.getBlock("AdditionalRails:railSwitchSignalOn");
        railFamily = (RailsUpdateFamily)blockManager.getBlockFamily("Rails:rails");
        invertFamily = (RailsUpdateFamily)blockManager.getBlockFamily("Rails:railsTBlockInverted");
    }

    /**
     * Updates railSwitchLever and rails around it, when user interacts the block.
     * @param event ActivateEvent, just used to differentiate from other events
     * @param entity The entity related to railSwitchLever
     */
    @ReceiveEvent(components = {BlockComponent.class, RailSwitchLeverComponent.class})
    public void railSwitchLeverAction(ActivateEvent event, EntityRef entity, BlockComponent blockComponent, RailSwitchLeverComponent rslComponent) {
        Vector3i blockLocation = new Vector3i(blockComponent.getPosition());

        //switch the block from on to off, from off to on; rslComponent.isOn is set in block prefab
        if (rslComponent.isOn) {
            worldProvider.setBlock(blockLocation, railSwitchLeverOff);
        } else {
            worldProvider.setBlock(blockLocation, railSwitchLeverOn);
        }

        //update rails based on switch state
        updateRails(blockLocation, rslComponent.isOn);
    }

    /**
     * Updates railSwitchSignal and rails around it, when {@link org.terasology.signalling} signal changes its state
     * @param event OnChangedComponent event raised by signal modyfying {@link org.terasology.signalling.components.SignalConsumerStatusComponent}
     * @param entity The entity related to railSwitchSignal
     */
    @ReceiveEvent(components = {BlockComponent.class, RailSwitchSignalComponent.class, SignalConsumerStatusComponent.class})
    public void railSwitchSignalAction(OnChangedComponent event, EntityRef entity, BlockComponent blockComponent, SignalConsumerStatusComponent scsComponent) {
        Vector3i blockLocation = new Vector3i(blockComponent.getPosition());
        Block block = worldProvider.getBlock(blockLocation);

        //switch the block from on to off, from off to on based on signal
        if (block == railSwitchSignalOn && !scsComponent.hasSignal) {
            worldProvider.setBlock(blockLocation, railSwitchSignalOff);
        } else if (block == railSwitchSignalOff && scsComponent.hasSignal) {
            worldProvider.setBlock(blockLocation, railSwitchSignalOn);
        }

        //update rails based on switch state
        updateRails(blockLocation, scsComponent.hasSignal);
    }

    /**
     * Looks for rails around switchLocation and updates them basing on inverted bool
     * @param switchLocation location of the switch
     * @param inverted boolean which is false if the rail juntion should be normal, or true, if the rail juntion should be inverted
     */
    private void updateRails(Vector3i switchLocation, boolean inverted) {
        //prepare the locations around the switch, where we're going to look for rails
        Vector3i[] railLocations = {new Vector3i(switchLocation.x, switchLocation.y, switchLocation.z+1), //north
                new Vector3i(switchLocation.x, switchLocation.y, switchLocation.z-1), //south
                new Vector3i(switchLocation.x+1, switchLocation.y, switchLocation.z), //west
                new Vector3i(switchLocation.x-1, switchLocation.y, switchLocation.z), //east
                new Vector3i(switchLocation.x, switchLocation.y+1, switchLocation.z)}; //above

        //iterate through all the positions and change the rail's state if needed
        for (Vector3i railLocation : railLocations) {
            Block block = worldProvider.getBlock(railLocation);
            EntityRef entity = block.getEntity();

            if (!entity.hasComponent(RailComponent.class)) {
                continue; //if the block is not a rail, go to the next iteration of the loop
            }

            //following code is based on Rails' module WrenchAction
            byte connections = Byte.parseByte(block.getURI().getIdentifier().toString());

            if (SideBitFlag.getSides(connections).size() == 3) {
                if (block.getBlockFamily() == railFamily || block.getBlockFamily() == invertFamily) {
                    blockEntityRegistry.setBlockForceUpdateEntity(railLocation, inverted ? invertFamily.getBlockByConnection(connections) : railFamily.getBlockByConnection(connections));
                }
            }
        }
    }
}
