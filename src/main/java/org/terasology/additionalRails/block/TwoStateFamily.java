package org.terasology.additionalRails.block;

import com.google.common.collect.Maps;
import net.logstash.logback.encoder.org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.math.Side;
import org.terasology.math.geom.Vector3i;
import org.terasology.naming.Name;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.family.AbstractBlockFamily;

import java.util.Map;

public class TwoStateFamily extends AbstractBlockFamily {
    private Map<Boolean, Block> blocks = Maps.newHashMap();

    public TwoStateFamily(BlockUri uri, Iterable<String> categories, Map<Boolean, Block> blocks) {
        super(uri, categories);
        for (Map.Entry<Boolean, Block> entry : blocks.entrySet()) {
            Block block = entry.getValue();
            Boolean key = entry.getKey();

            block.setBlockFamily(this);
            block.setUri(new BlockUri(uri, new Name(key.toString())));

            this.blocks.put(key, block);
        }
    }

    public boolean getState(Block block) {
        return Boolean.valueOf(block.getURI().getIdentifier().toString());
    }

    public Block getBlockForState(boolean state) {
        return blocks.get(state);
    }

    @Override
    public Block getBlockForPlacement(WorldProvider worldProvider, BlockEntityRegistry blockEntityRegistry, Vector3i location, Side attachmentSide, Side direction) {
        return blocks.get(false);
    }

    @Override
    public Block getArchetypeBlock() {
        return blocks.get(false);
    }

    @Override
    public Block getBlockFor(BlockUri blockUri) {
        return blocks.get(BooleanUtils.toBooleanObject(blockUri.getIdentifier().toString()));
    }

    @Override
    public Iterable<Block> getBlocks() {
        return blocks.values();
    }
}
