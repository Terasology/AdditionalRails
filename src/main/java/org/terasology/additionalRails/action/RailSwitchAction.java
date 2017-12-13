package org.terasology.additionalRails.action;

import org.terasology.additionalRails.components.RailSwitchComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.signalling.components.SignalConsumerComponent;
import org.terasology.signalling.components.SignalConsumerStatusComponent;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.block.BlockManager;

@RegisterSystem(RegisterMode.AUTHORITY)
public class RailSwitchAction extends BaseComponentSystem {

    @In
    private WorldProvider worldProvider;
    @In
    private BlockEntityRegistry blockEntityRegistry;

    private Block railSwitchSignalOff;
    private Block railSwitchSignalOn;

    @Override
    public void initialise() {
        final BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        railSwitchSignalOff = blockManager.getBlock("AdditionalRails:railSwitchSignalOff");
        railSwitchSignalOn = blockManager.getBlock("AdditionalRails:railSwitchSignalOn");
    }

    @ReceiveEvent(components = {SignalConsumerStatusComponent.class, RailSwitchComponent.class})
    public void switchAction(OnChangedComponent event, EntityRef entity) {
        if (entity.hasComponent(BlockComponent.class)) {
            SignalConsumerStatusComponent consumerStatusComponent = entity.getComponent(SignalConsumerStatusComponent.class);
            Vector3i blockLocation = new Vector3i(entity.getComponent(BlockComponent.class).getPosition());
            Block block = worldProvider.getBlock(blockLocation);

            SignalConsumerComponent signalConsumerComponent = entity.getComponent(SignalConsumerComponent.class);

            if (block == railSwitchSignalOff && consumerStatusComponent.hasSignal) {
                railSwitchSignalOn.setKeepActive(true);
                worldProvider.setBlock(blockLocation, railSwitchSignalOn);
            } else if (block == railSwitchSignalOn && !consumerStatusComponent.hasSignal) {
                railSwitchSignalOff.setKeepActive(true);
                worldProvider.setBlock(blockLocation, railSwitchSignalOn);
            }

            RailSwitchComponent railSwitchComponent = entity.getComponent(RailSwitchComponent.class);
            blockEntityRegistry.getBlockEntityAt(blockLocation).addOrSaveComponent(signalConsumerComponent);
            blockEntityRegistry.getBlockEntityAt(blockLocation).addOrSaveComponent(railSwitchComponent);
            worldProvider.getBlock(blockLocation).setKeepActive(true);
        }
    }
}
