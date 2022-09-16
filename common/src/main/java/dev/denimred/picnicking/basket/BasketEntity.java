package dev.denimred.picnicking.basket;

import dev.architectury.networking.NetworkManager;
import dev.denimred.picnicking.basket.BasketParts.Handles;
import dev.denimred.picnicking.init.PicnicEntityTypes;
import dev.denimred.picnicking.init.PicnicItems;
import dev.denimred.picnicking.util.EntityContainerOpenersCounter;
import dev.denimred.picnicking.util.WrappedMenuContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static net.minecraft.world.entity.EntityType.ENTITY_TAG;

// TODO: Add basket physics (basically copy boat physics)
// TODO: Make the basket breakable in survival
// TODO: Test Forge Capabilities and Fabric's container system (will probably need work?)
// TODO: Add picnic buff thing
// TODO: Clean up data syncing?
// TODO: Add variants? (wood colors, blanket colors)
@ParametersAreNonnullByDefault
public class BasketEntity extends Entity implements WrappedMenuContainer {
    private static final EntityDataAccessor<Boolean> HANDLES_UP_DATA_ID = SynchedEntityData.defineId(BasketEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FRONT_LID_OPEN_DATA_ID = SynchedEntityData.defineId(BasketEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BACK_LID_OPEN_DATA_ID = SynchedEntityData.defineId(BasketEntity.class, EntityDataSerializers.BOOLEAN);

    public final BasketParts parts = new BasketParts(this);

    public BasketEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        entityData.define(HANDLES_UP_DATA_ID, parts.handles.isUp());
        entityData.define(FRONT_LID_OPEN_DATA_ID, parts.frontLid.isOpen());
        entityData.define(BACK_LID_OPEN_DATA_ID, parts.backLid.isOpen());
    }

    public static EntityType.Builder<BasketEntity> createType() {
        return EntityType.Builder.of(BasketEntity::new, MobCategory.MISC).sized(0.875f, 0.5f);
    }

    public static InteractionResult place(UseOnContext context) {
        Level level = context.getLevel();
        Vec3 pos = context.getClickLocation();
        BasketEntity basket = PicnicEntityTypes.PICNIC_BASKET.get().create(level);
        if (basket == null) return InteractionResult.FAIL;
        // Get a position that won't result in the basket being inside a block
        Vec3 freePos = getFreeBasketPos(level, pos, basket);
        if (freePos == null) return InteractionResult.FAIL;
        // All placement checks passed, so assume success on the client
        if (level.isClientSide) return InteractionResult.SUCCESS;
        // Load the basket from the item and move it to the correct spot
        ItemStack stack = context.getItemInHand();
        basket.loadFromItem(stack);
        basket.moveTo(freePos.x, freePos.y, freePos.z, Mth.wrapDegrees(context.getRotation() + 180.0f), 0.0f);
        // Decrement the basket stack if the player isn't in creative (or if not placed by player)
        Player player = context.getPlayer();
        if (player == null || !player.getAbilities().instabuild) stack.shrink(1);
        // Actually spawn the basket and consume the interaction
        level.addFreshEntity(basket);
        return InteractionResult.CONSUME;
    }

    public static Vec3 getFreeBasketPos(Level level, Vec3 pos, Entity basket) {
        EntityDimensions dimensions = basket.getDimensions(Pose.STANDING);
        double width = dimensions.width;
        double height = dimensions.height;
        double adjustedWidth = Math.max(0.0F, width) + 1.0E-6;
        double adjustedHeight = Math.max(0.0F, height) + 1.0E-6;
        VoxelShape voxelShape = Shapes.create(AABB.ofSize(pos, adjustedWidth, adjustedHeight, adjustedWidth));
        Optional<Vec3> freePos = level.findFreePosition(basket, voxelShape, pos, width, height, width);
        return freePos.map(v -> new Vec3(v.x, Math.min(v.y, pos.y), v.z)).orElse(null);
    }

    public void loadFromItem(ItemStack stack) {
        CompoundTag itemTag = stack.getTagElement(ENTITY_TAG);
        if (itemTag != null) {
            CompoundTag basketTag = saveWithoutId(new CompoundTag());
            UUID newUUID = getUUID();
            basketTag.merge(itemTag);
            load(basketTag);
            setUUID(newUUID);
        }
        setCustomName(stack.hasCustomHoverName() ? stack.getHoverName() : null);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) {
            parts.tick();
        } else {
            if (tickCount % 10 == 0) openersCounter.recheckOpeners();
        }
    }

    @Override
    public boolean isPickable() {
        return !isRemoved(); // Can be picked (for interaction, attacking, etc.)
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return PicnicItems.PICNIC_BASKET.get().getDefaultInstance();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            if (!player.getItemInHand(hand).isEmpty()) return InteractionResult.PASS;
            if (level.isClientSide) return InteractionResult.SUCCESS;
            ItemStack stack = BasketItem.createStack(this);
            discard();
            player.setItemInHand(hand, stack);
        } else {
            if (level.isClientSide) return InteractionResult.SUCCESS;
            player.openMenu(this);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        if (entity instanceof Player player) {
            // Prevent player from attacking basket if they cannot interact with its position
            if (!level.mayInteract(player, blockPosition())) return true;
            // If player shift-clicked with an empty hand, toggle handles instead of attacking
            if (player.isShiftKeyDown() && player.getMainHandItem().isEmpty()) {
                parts.handles.flip();
                playBambooPlaceSound(1.0f);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) {
            return false;
        } else if (!level.isClientSide && !isRemoved()) {
            markHurt();
            Entity entity = source.getEntity();
            if (entity instanceof Player player && player.getAbilities().instabuild) {
                if (level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    spawnAtLocation(BasketItem.createStack(this));
                }
                kill();
            } else {
                gameEvent(GameEvent.ENTITY_DAMAGED, entity);
            }
            return true;
        } else {
            return true;
        }
    }

    @Override
    public boolean isPushable() {
        return true; // Can push (and be pushed by) other entities
    }

    @Override
    protected MovementEmission getMovementEmission() {
        return MovementEmission.NONE; // It's a basket, don't emit sounds or events for skulks/wardens
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height; // Eye height affects getFreeBasketPos, so return the full height for desired effect
    }

    @Override
    protected void defineSynchedData() {
        // Mojang is silly, so we're not using this
        // See constructor for synced data definitions
    }

    public void syncParts() {
        entityData.set(HANDLES_UP_DATA_ID, parts.handles.isUp());
        entityData.set(FRONT_LID_OPEN_DATA_ID, parts.frontLid.isOpen());
        entityData.set(BACK_LID_OPEN_DATA_ID, parts.backLid.isOpen());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (key.equals(HANDLES_UP_DATA_ID)) {
            parts.handles.setUp(entityData.get(HANDLES_UP_DATA_ID));
        } else if (key.equals(FRONT_LID_OPEN_DATA_ID)) {
            parts.frontLid.setOpen(entityData.get(FRONT_LID_OPEN_DATA_ID));
        } else if (key.equals(BACK_LID_OPEN_DATA_ID)) {
            parts.backLid.setOpen(entityData.get(BACK_LID_OPEN_DATA_ID));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        container.load(tag);
        if (tag.contains(Handles.UP_TAG, Tag.TAG_BYTE)) parts.handles.setUp(tag.getBoolean(Handles.UP_TAG));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        container.save(tag);
        tag.putBoolean(Handles.UP_TAG, parts.handles.isUp());
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkManager.createAddEntityPacket(this);
    }

    private final BasketContainer container = new BasketContainer(BasketContainer.BASKET_SIZE);

    private final EntityContainerOpenersCounter<?> openersCounter = new EntityContainerOpenersCounter<>(this) {
        @Override
        protected void onOpen(@Nullable Player player) {
            if (player != null) parts.openFacingLid(player);
            playOpenSound();
        }

        @Override
        protected void onClose(@Nullable Player player) {
            playCloseSound();
        }

        @Override
        protected void openerCountChanged(int oldCount, int newCount) {
            if (newCount <= 0) parts.closeLids();
        }
    };

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public EntityContainerOpenersCounter<?> getOpenersCounter() {
        return openersCounter;
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new BasketMenu(id, inventory, this);
    }

    public void playOpenSound() {
        playBambooPlaceSound(0.9f);
    }

    public void playCloseSound() {
        playBambooPlaceSound(0.4f);
    }

    protected void playBambooPlaceSound(float pitchOffset) {
        playSound(SoundEvents.BAMBOO_PLACE, 0.75f, level.random.nextFloat() * 0.1f + pitchOffset);
    }
}
