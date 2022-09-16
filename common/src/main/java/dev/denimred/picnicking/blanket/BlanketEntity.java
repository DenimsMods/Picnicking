package dev.denimred.picnicking.blanket;

import dev.architectury.hooks.level.entity.PlayerHooks;
import dev.architectury.networking.NetworkManager;
import dev.denimred.picnicking.init.PicnicEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

// TODO: Add recipe for blanket
// TODO: Let players dismount blanket by moving/jumping as well as sneaking?
@ParametersAreNonnullByDefault
public class BlanketEntity extends Entity {
    public BlanketEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public static EntityType.Builder<BlanketEntity> createType() {
        return EntityType.Builder.of(BlanketEntity::new, MobCategory.MISC)
                .fireImmune()
                .noSummon()
                .updateInterval(Integer.MAX_VALUE)
                .sized(0.25f, 0.25f);
    }

    public static void summonAndSeat(Level level, Vec3 pos, Entity passenger) {
        // Cannot summon on clients
        if (level.isClientSide) return;
        // Do some quick collision checks and adjustments to try and get a safe seat position
        Vec3 safePos = getSafeSeatPosition(level, pos, passenger);
        // Create and summon the blanket entity
        BlanketEntity blanketEntity = PicnicEntityTypes.PICNIC_BLANKET.get().create(level);
        if (blanketEntity == null) return;
        blanketEntity.noPhysics = true;
        blanketEntity.setPos(safePos);
        level.addFreshEntity(blanketEntity);
        // Seat the passenger
        passenger.startRiding(blanketEntity, true);
        if (passenger instanceof TamableAnimal animal) animal.setInSittingPose(true);
    }

    public static Vec3 getSafeSeatPosition(Level level, Vec3 pos, Entity passenger) {
        EntityDimensions dimensions = passenger.getDimensions(Pose.STANDING);
        double width = dimensions.width;
        double height = dimensions.height;
        double adjustedWidth = Math.max(0.0F, width) + 1.0E-6;
        double adjustedHeight = Math.max(0.0F, height) + 1.0E-6;
        VoxelShape voxelShape = Shapes.create(AABB.ofSize(pos, adjustedWidth, adjustedHeight, adjustedWidth));
        Optional<Vec3> freePos = level.findFreePosition(passenger, voxelShape, pos, width, height, width).map(v -> new Vec3(v.x, pos.y, v.z));
        return freePos.orElse(pos);
    }

    @Override
    public void tick() {
        if (level.isClientSide) return;
        Block block = level.getBlockState(blockPosition()).getBlock();
        if (block instanceof BlanketBlock && isVehicle()) return;
        discard();
    }

    @Override
    public void setDeltaMovement(Vec3 motion) {
        // no-op
    }

    @Override
    protected boolean canRide(Entity entity) {
        return !(entity instanceof Player player) || !PlayerHooks.isFake(player);
    }

    @Override
    public double getPassengersRidingOffset() {
        return -0.25d;
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return position();
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (passenger instanceof TamableAnimal animal) animal.setInSittingPose(false);
    }

    @Override
    protected void defineSynchedData() {
        // no-op
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        // no-op
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        // no-op
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkManager.createAddEntityPacket(this);
    }
}
