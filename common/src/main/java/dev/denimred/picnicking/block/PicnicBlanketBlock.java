package dev.denimred.picnicking.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;

public class PicnicBlanketBlock extends CarpetBlock {
    public PicnicBlanketBlock() {
        this(Properties.copy(Blocks.RED_CARPET));
    }

    public PicnicBlanketBlock(Properties properties) {
        super(properties);
    }
}
