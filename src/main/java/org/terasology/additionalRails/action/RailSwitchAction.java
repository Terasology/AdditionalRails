package org.terasology.additionalRails.action;

import org.terasology.additionalRails.components.RailSwitchComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.Event;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.console.Console;
import org.terasology.math.SideBitFlag;
import org.terasology.minecarts.blocks.RailComponent;
import org.terasology.minecarts.blocks.RailsUpdateFamily;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.signalling.components.SignalConsumerStatusComponent;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.family.BlockFamily;

import java.text.MessageFormat;

@RegisterSystem(RegisterMode.AUTHORITY)
public class RailSwitchAction extends BaseComponentSystem {

    @In
    private WorldProvider worldProvider;
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private Console console;

    private Block railSwitchLeverOff;
    private Block railSwitchLeverOn;
    private Block railSwitchSignalOff;
    private Block railSwitchSignalOn;
    private Block railSwitchCombinedOff;
    private Block railSwitchCombinedOn;
    private RailsUpdateFamily railFamily;
    private RailsUpdateFamily invertFamily;

    private enum ActionSource {ACTIVATE, SIGNAL};

    @Override
    public void initialise() {
        final BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        railSwitchLeverOff = blockManager.getBlock("AdditionalRails:railSwitchLeverOff");
        railSwitchLeverOn = blockManager.getBlock("AdditionalRails:railSwitchLeverOn");
        railSwitchSignalOff = blockManager.getBlock("AdditionalRails:railSwitchSignalOff");
        railSwitchSignalOn = blockManager.getBlock("AdditionalRails:railSwitchSignalOn");
        railSwitchCombinedOff = blockManager.getBlock("AdditionalRails:railSwitchCombinedOff");
        railSwitchCombinedOn = blockManager.getBlock("AdditionalRails:railSwitchCombinedOn");
        railFamily = (RailsUpdateFamily)blockManager.getBlockFamily("Rails:rails");
        invertFamily = (RailsUpdateFamily)blockManager.getBlockFamily("Rails:railsTBlockInverted");
    }

    @ReceiveEvent(components = {BlockComponent.class, RailSwitchComponent.class})
    public void activateAction(ActivateEvent event, EntityRef entity) {
        RailSwitchComponent railSwitchComponent = entity.getComponent(RailSwitchComponent.class);

        updateSwitch(event, entity, railSwitchComponent, !railSwitchComponent.isOn, ActionSource.ACTIVATE);
    }

    @ReceiveEvent(components = {BlockComponent.class, RailSwitchComponent.class, SignalConsumerStatusComponent.class})
    public void signalAction(OnChangedComponent event, EntityRef entity) {
        SignalConsumerStatusComponent consumerStatusComponent = entity.getComponent(SignalConsumerStatusComponent.class);
        RailSwitchComponent railSwitchComponent = entity.getComponent(RailSwitchComponent.class);

        if (consumerStatusComponent.hasSignal != railSwitchComponent.hasSignal) {
            updateSwitch(event, entity, railSwitchComponent, consumerStatusComponent.hasSignal, ActionSource.SIGNAL);
        }
    }

    private void updateSwitch(Event event, EntityRef entity, RailSwitchComponent oldRailComponent, boolean input, ActionSource actionSource) {
        Vector3i blockLocation = new Vector3i(entity.getComponent(BlockComponent.class).getPosition());
        switch (oldRailComponent.mode) {
            case 0:
                if (input) {
                    railSwitchLeverOn.setKeepActive(true);
                    worldProvider.setBlock(blockLocation, railSwitchLeverOn);
                } else {
                    railSwitchLeverOff.setKeepActive(true);
                    worldProvider.setBlock(blockLocation, railSwitchLeverOff);
                }
                break;
            case 1:
                if (input) {
                    railSwitchSignalOn.setKeepActive(true);
                    worldProvider.setBlock(blockLocation, railSwitchSignalOn);
                } else {
                    railSwitchSignalOff.setKeepActive(true);
                    worldProvider.setBlock(blockLocation, railSwitchSignalOff);
                }
                break;

            case 2: //TODO: railSwitch using both activate (E button) and Signalling mode
                /*
                SignalConsumerStatusComponent oldStatusComponent = entity.getComponent(SignalConsumerStatusComponent.class);
                SignalConsumerStatusComponent newStatusComponent = new SignalConsumerStatusComponent();
                RailSwitchComponent newRailComponent = new RailSwitchComponent();

                if (actionSource == ActionSource.ACTIVATE) {
                    newStatusComponent.hasSignal = oldStatusComponent.hasSignal;
                    newRailComponent.hasSignal = oldStatusComponent.hasSignal;
                    newRailComponent.isOn = input;
                }
                if (actionSource == ActionSource.SIGNAL) {
                    newStatusComponent.hasSignal = input;
                    newRailComponent.hasSignal = input;
                    newRailComponent.isOn = oldRailComponent.isOn;
                }
                newRailComponent.mode = 2;

                if (input || oldRailComponent.isOn) {
                    railSwitchCombinedOn.setKeepActive(true);
                    worldProvider.setBlock(blockLocation, railSwitchCombinedOn);

                } else {
                    railSwitchCombinedOff.setKeepActive(true);
                    worldProvider.setBlock(blockLocation, railSwitchCombinedOff);
                }

                EntityRef newEntity = blockEntityRegistry.getBlockEntityAt(blockLocation);
                newEntity.addOrSaveComponent(newRailComponent);
                newEntity.addOrSaveComponent(newStatusComponent);

                RailSwitchComponent rscTest = newEntity.getComponent(RailSwitchComponent.class);
                SignalConsumerStatusComponent scscTest = newEntity.getComponent(SignalConsumerStatusComponent.class);

                //console.addMessage("["+rscTest.mode+","+rscTest.isOn+","+rscTest.hasSignal+","+scscTest.hasSignal+"]");
                */
                break;
        }
        worldProvider.getBlock(blockLocation).setKeepActive(true);

        Vector3i[] railPositions = {new Vector3i(blockLocation.x, blockLocation.y, blockLocation.z+1), //north
                                    new Vector3i(blockLocation.x, blockLocation.y, blockLocation.z-1), //south
                                    new Vector3i(blockLocation.x+1, blockLocation.y, blockLocation.z), //west
                                    new Vector3i(blockLocation.x-1, blockLocation.y, blockLocation.z), //east
                                    new Vector3i(blockLocation.x, blockLocation.y+1, blockLocation.z)}; //up

        for(Vector3i vector : railPositions) {
            Block block = worldProvider.getBlock(vector);
            EntityRef railEntity = block.getEntity();
            if (!railEntity.hasComponent(RailComponent.class)) {
                continue;
            }

            byte connections = Byte.parseByte(block.getURI().getIdentifier().toString());

            if (SideBitFlag.getSides(connections).size() == 3) {
                if (block.getBlockFamily() == railFamily || block.getBlockFamily() == invertFamily) {
                    blockEntityRegistry.setBlockForceUpdateEntity(vector, input ? railFamily.getBlockByConnection(connections) : invertFamily.getBlockByConnection(connections));
                }
            }
        }

    }

}

