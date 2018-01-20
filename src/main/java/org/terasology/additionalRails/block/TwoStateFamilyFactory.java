package org.terasology.additionalRails.block;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockBuilderHelper;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.family.BlockFamily;
import org.terasology.world.block.family.BlockFamilyFactory;
import org.terasology.world.block.family.RegisterBlockFamilyFactory;
import org.terasology.world.block.loader.BlockFamilyDefinition;

import java.util.Map;
import java.util.Set;

@RegisterBlockFamilyFactory("twoState")
public class TwoStateFamilyFactory implements BlockFamilyFactory {
    private static final Logger logger = LoggerFactory.getLogger(TwoStateFamilyFactory.class);
    private static final ImmutableSet<String> BLOCK_NAMES = ImmutableSet.of("false", "true");

    @Override
    public BlockFamily createBlockFamily(BlockFamilyDefinition definition, BlockBuilderHelper blockBuilder) {
        Map<Boolean, Block> blocks = Maps.newHashMap();
        blocks.put(false, blockBuilder.constructSimpleBlock(definition, "false"));
        blocks.put(true, blockBuilder.constructSimpleBlock(definition, "true"));

        return new TwoStateFamily(new BlockUri(definition.getUrn()), definition.getCategories(), blocks);
    }

    @Override
    public Set<String> getSectionNames() {
        return BLOCK_NAMES;
    }
}
