package dev.denimred.picnicking.thermos;

import dev.denimred.picnicking.init.PicnicItems;
import dev.denimred.picnicking.item.ChargeableItem;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static dev.denimred.picnicking.Picnicking.MOD_ID;
import static dev.denimred.picnicking.init.PicnicItems.Tags.THERMOS_DRINKABLES;
import static net.minecraft.nbt.Tag.TAG_COMPOUND;

// TODO: Add recipes for thermos
// TODO: Add multiple color options for the thermos
// TODO: Allow multiple drinks to be stored in the thermos
// TODO: Add a "chug" mode to the thermos
//       - Uses every type of item at least once
//       - If an item restores food, uses as much as it can to fill hunger
@ParametersAreNonnullByDefault
public class ThermosItem extends ChargeableItem {
    public static final String TAG_DRINK = "Drink";
    public static final int MAX_CHARGES = 16;

    public ThermosItem() {
        this(PicnicItems.properties().stacksTo(1));
    }

    public ThermosItem(Properties properties) {
        super(properties);
    }

    // ******************** NBT/STORAGE ********************

    public ItemStack getDrink(ItemStack thermos) {
        CompoundTag tag = thermos.getTagElement(MOD_ID);
        if (tag == null) return ItemStack.EMPTY;
        if (!tag.contains(TAG_DRINK, TAG_COMPOUND)) return ItemStack.EMPTY;
        return ItemStack.of(tag.getCompound(TAG_DRINK));
    }

    public ItemStack fillWithDrink(ItemStack thermos, ItemStack drink, int amount) {
        if (!drink.is(THERMOS_DRINKABLES)) return thermos;
        if (amount < 1) return thermos;
        CompoundTag tag = thermos.getOrCreateTagElement(MOD_ID);
        CompoundTag drinkTag = drink.save(new CompoundTag());
        tag.put(TAG_DRINK, drinkTag);
        setCharge(thermos, amount);
        return thermos;
    }

    @Override
    public int getMaxCharge(ItemStack stack) {
        return MAX_CHARGES;
    }

    @Override
    public void clearCharge(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(MOD_ID);
        if (tag == null) return;
        tag.remove(TAG_DRINK);
        super.clearCharge(stack);
    }

    // ******************** INSTANTIATION ********************

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (!allowdedIn(tab)) return;
        items.add(getDefaultInstance());
        for (Holder<Item> holder : Registry.ITEM.getTagOrEmpty(THERMOS_DRINKABLES)) {
            Item drinkable = holder.value();
            if (drinkable instanceof PotionItem) {
                for (Potion potion : Registry.POTION) {
                    if (potion == Potions.EMPTY) continue;
                    ItemStack thermos = getDefaultInstance();
                    ItemStack drink = PotionUtils.setPotion(new ItemStack(drinkable), potion);
                    items.add(fillWithDrink(thermos, drink, getMaxCharge(thermos)));
                }
            } else {
                ItemStack thermos = getDefaultInstance();
                items.add(fillWithDrink(thermos, drinkable.getDefaultInstance(), getMaxCharge(thermos)));
            }
        }
    }

    // ******************** USAGE ********************

    @Override
    public boolean isUsable(ItemStack stack) {
        return super.isUsable(stack) && !getDrink(stack).isEmpty();
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return EAT_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return isUsable(stack) ? UseAnim.DRINK : UseAnim.NONE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!isUsable(stack)) return InteractionResultHolder.fail(stack);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!isUsable(stack)) return stack;

        ItemStack drink = getDrink(stack);
        Player player = entity instanceof Player ? (Player) entity : null;

        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, drink);
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) player, stack);
        }

        if (player != null) {
            player.getFoodData().eat(drink.getItem(), drink);
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) reduceCharge(stack);
        }

        if (!level.isClientSide) {
            for (MobEffectInstance effect : PotionUtils.getMobEffects(drink)) {
                if (effect.getEffect().isInstantenous()) {
                    effect.getEffect().applyInstantenousEffect(player, player, entity, effect.getAmplifier(), 1.0);
                } else {
                    entity.addEffect(new MobEffectInstance(effect));
                }
            }
        }

        level.gameEvent(entity, GameEvent.DRINKING_FINISH, entity.eyeBlockPosition());
        return stack;
    }

    // ******************** VISUALS ********************

    @Override
    public int getBarColor(ItemStack stack) {
        ItemStack drink = getDrink(stack);
        // Some hardcoded special cases
        if (drink.is(Items.MILK_BUCKET)) return 0xFFFFFF;
        if (drink.is(Items.MUSHROOM_STEW)) return 0xCC9978;
        if (drink.is(Items.RABBIT_STEW)) return 0xE29C4A;
        if (drink.is(Items.SUSPICIOUS_STEW)) return 0xA8D475;
        if (drink.is(Items.BEETROOT_SOUP)) return 0xB82A30;
        if (drink.is(Items.HONEY_BOTTLE)) return 0xFFD32D;
        // Use potion color if available (will fall back to a pink invalid color)
        // TODO: Make the default thermos bar color different so new items don't give a hideous color
        return PotionUtils.getColor(drink);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || getDrink(stack).hasFoil();
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        ItemStack drink = getDrink(stack);
        String descriptionId = super.getDescriptionId(stack);
        if (drink.isEmpty()) return descriptionId + ".empty";
        return descriptionId;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        ItemStack drink = getDrink(stack);
        if (drink.isEmpty()) return;
        tooltip.add(drink.getHoverName().copy().append(" (%s/%s)".formatted(getCharge(stack), getMaxCharge(stack))).withStyle(ChatFormatting.GRAY));
        drink.getItem().appendHoverText(drink, level, tooltip, flag);
    }
}
