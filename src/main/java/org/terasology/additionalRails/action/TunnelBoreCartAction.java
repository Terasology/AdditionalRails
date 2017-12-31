package org.terasology.additionalRails.action;

import org.terasology.additionalRails.components.TunnelBoreCartComponent;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.inventory.InventoryComponent;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.math.Side;
import org.terasology.math.SideBitFlag;
import org.terasology.math.geom.Vector3i;
import org.terasology.minecarts.blocks.RailComponent;
import org.terasology.minecarts.blocks.RailsUpdateFamily;
import org.terasology.minecarts.components.RailVehicleComponent;
import org.terasology.registry.CoreRegistry;
import org.terasology.registry.In;
import org.terasology.segmentedpaths.components.PathFollowerComponent;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.items.BlockItemComponent;

import java.util.EnumSet;
import java.util.List;

@RegisterSystem(RegisterMode.AUTHORITY)
public class TunnelBoreCartAction extends BaseComponentSystem implements UpdateSubscriberSystem {

    @In
    EntityManager entityManager;
    @In
    WorldProvider worldProvider;

    @Override
    public void update(float delta) {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        Block air = blockManager.getBlock(BlockManager.AIR_ID);
        
        for (EntityRef entity : entityManager.getEntitiesWith(TunnelBoreCartComponent.class, RailVehicleComponent.class, PathFollowerComponent.class, InventoryComponent.class)) {
            PathFollowerComponent pfComp = entity.getComponent(PathFollowerComponent.class);
            EntityRef rbEntity = pfComp.segmentMeta.association;

            if (!rbEntity.hasComponent(RailComponent.class)) {
                continue;
            }

            BlockComponent bComp = rbEntity.getComponent(BlockComponent.class);
            Vector3i rbLocation = new Vector3i(bComp.getPosition());
            Block rBlock = bComp.getBlock();

            byte connections = Byte.parseByte(rBlock.getURI().getIdentifier().toString());
            EnumSet<Side> sides = SideBitFlag.getSides(connections);
            if (sides.size() != 1) {
                continue;
            }
            Side side = sides.iterator().next();
            if (!side.isHorizontal()) {
                continue;
            }

            RailsUpdateFamily ruFamily = (RailsUpdateFamily)rBlock.getBlockFamily();
            rBlock = ruFamily.getBlockByConnection(connections);
            side = side.reverse();
            Vector3i nextBlock = rbLocation.add(side.getVector3i());
            Vector3i digCenter = new Vector3i(nextBlock.x, nextBlock.y+1, nextBlock.z);
            
            Vector3i right = side.yawClockwise(1).getVector3i();
            Vector3i left = side.yawClockwise(3).getVector3i();
            
            worldProvider.setBlock(digCenter, air);
            worldProvider.setBlock(digCenter.add(Vector3i.up()), air);
            worldProvider.setBlock(digCenter.add(right), air);
            worldProvider.setBlock(digCenter.add(Vector3i.down()), air);
            worldProvider.setBlock(digCenter.add(Vector3i.down()), air);
            if(!worldProvider.getBlock(digCenter.add(left)).getBlockFamily().equals(ruFamily))
                worldProvider.setBlock(digCenter, air);
            worldProvider.setBlock(digCenter.add(left), air);
            worldProvider.setBlock(digCenter.add(Vector3i.up()), air);
            worldProvider.setBlock(digCenter.add(Vector3i.up()), air);

            if(worldProvider.getBlock(nextBlock).getBlockFamily().equals(ruFamily)) {
                continue;
            }
            
            Block underNextBlock = worldProvider.getBlock(new Vector3i(nextBlock).add(Vector3i.down()));
            if (underNextBlock.isPenetrable() || underNextBlock.isLiquid()) {
                continue;
            }

            InventoryComponent iComponent = entity.getComponent(InventoryComponent.class);
            List<EntityRef> slots = iComponent.itemSlots;

            boolean gotItem = false;
            for (EntityRef slot : slots) {
                if (slot == EntityRef.NULL || !slot.hasComponent(ItemComponent.class) || !slot.hasComponent(BlockItemComponent.class)) {
                    continue;
                }
                ItemComponent item = slot.getComponent(ItemComponent.class);
                BlockItemComponent bitem = slot.getComponent(BlockItemComponent.class);
                if (bitem.blockFamily.equals(ruFamily)) {
                    item.stackCount--;
                    gotItem = true;
                    if (item.stackCount <= 0) {
                        iComponent.itemSlots.set(iComponent.itemSlots.indexOf(slot), EntityRef.NULL);
                    }
                    break;
                }
            }

            if (gotItem) {
                worldProvider.setBlock(nextBlock, rBlock);
            }
        }
    }

}