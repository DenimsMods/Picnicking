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

// TODO: Add variants? (16 colors)
@ParametersAreNonnullByDefault
public class BlanketBlock extends CarpetBlock {
    public BlanketBlock() {
        this(Properties.copy(Blocks.RED_CARPET));
    }

    public BlanketBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // Standard shift-click use override
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        // Don't sit if already sitting (helps avoid mis-clicks causing you to change spot)
        if (player.isPassenger()) return InteractionResult.PASS;
        // Only allow sitting when interacting with the top
        if (hit.getDirection() != Direction.UP) return InteractionResult.PASS;
        // Prevent passenger from potentially suffocating
        BlockPos above = pos.above();
        if (level.getBlockState(above).isSuffocating(level, above)) return InteractionResult.PASS;
        // Summon the entity and seat the passenger
        BlanketEntity.summonAndSeat(level, hit.getLocation(), player);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
