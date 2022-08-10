package dev.denimred.picnicking.blanket;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PicnicBlanketBlock extends CarpetBlock {
    public PicnicBlanketBlock() {
        this(Properties.copy(Blocks.RED_CARPET));
    }

    public PicnicBlanketBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // Standard shift-click use override
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        // Don't sit if already sitting (helps avoid mis-clicks causing you to chance spot)
        if (player.isPassenger()) return InteractionResult.PASS;
        // Only allow sitting when interacting with the top
        if (hit.getDirection() != Direction.UP) return InteractionResult.PASS;
        // Prevent passenger from potentially suffocating
        BlockPos above = pos.above();
        if (level.getBlockState(above).isSuffocating(level, above)) return InteractionResult.PASS;
        // Summon and seat the passenger
        PicnicBlanketEntity.summonAndSeat(level, hit.getLocation(), player);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
