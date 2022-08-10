package dev.denimred.picnicking.util.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

import static dev.denimred.picnicking.Picnicking.MOD_ID;

@ParametersAreNonnullByDefault
public abstract class ChargeableItem extends Item {
    public static final String TAG_CHARGE = "Charge";

    public ChargeableItem(Properties properties) {
        super(properties);
    }

    // ******************** NBT/STORAGE ********************

    abstract public int getMaxCharge(ItemStack stack);

    public void reduceCharge(ItemStack stack) {
        modifyCharge(stack, -1);
    }

    public void increaseCharge(ItemStack stack) {
        modifyCharge(stack, 1);
    }

    public void modifyCharge(ItemStack stack, int chargeOffset) {
        CompoundTag tag = stack.getOrCreateTagElement(MOD_ID);
        int storedCharge = tag.getInt(TAG_CHARGE);
        setCharge(stack, storedCharge + chargeOffset);
    }

    public void setCharge(ItemStack stack, int newCharge) {
        if (newCharge <= 0) {
            clearCharge(stack);
        } else {
            CompoundTag tag = stack.getOrCreateTagElement(MOD_ID);
            int charge = clampCharge(stack, newCharge);
            tag.putInt(TAG_CHARGE, charge);
        }
    }

    public void clearCharge(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(MOD_ID);
        if (tag == null) return;
        tag.remove(TAG_CHARGE);
        if (tag.isEmpty()) stack.removeTagKey(MOD_ID);
    }

    public int getCharge(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(MOD_ID);
        if (tag == null) return 0;
        int storedCharge = tag.getInt(TAG_CHARGE);
        return clampCharge(stack, storedCharge);
    }

    public boolean isFullyCharged(ItemStack stack) {
        return getCharge(stack) >= getMaxCharge(stack);
    }

    public int clampCharge(ItemStack stack, int charge) {
        return Mth.clamp(charge, 0, getMaxCharge(stack));
    }

    // ******************** ITEM USAGE ********************

    public boolean isUsable(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(MOD_ID);
        if (tag == null) return false;
        return tag.getInt(TAG_CHARGE) > 0;
    }

    // ******************** VISUALS ********************

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isUsable(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        CompoundTag tag = stack.getTagElement(MOD_ID);
        if (tag == null) return 1;
        return Math.round((float) tag.getInt(TAG_CHARGE) / (float) getMaxCharge(stack) * 13.0f);
    }
}
