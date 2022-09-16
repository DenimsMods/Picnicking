package dev.denimred.picnicking.basket;

import dev.denimred.picnicking.mixin.SimpleContainerAccessor;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class BasketContainer extends SimpleContainer {
    public static final int BASKET_SIZE = 10;

    public BasketContainer(int count) {
        super(count);
    }

    public NonNullList<ItemStack> getItems() {
        return ((SimpleContainerAccessor) this).accessItems();
    }

    public void load(CompoundTag tag) {
        ContainerHelper.loadAllItems(tag, getItems());
    }

    public void save(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, getItems());
    }
}
