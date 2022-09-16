package dev.denimred.picnicking.util;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.IntStream;

@ParametersAreNonnullByDefault
public interface WrappedMenuContainer extends MenuProvider, WorldlyContainer {
    Container getContainer();

    EntityContainerOpenersCounter<?> getOpenersCounter();

    AbstractContainerMenu createMenu(int id, Inventory inventory);

    @Nullable
    @Override
    default AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return createMenu(i, inventory);
    }

    @Override
    default int getContainerSize() {
        return getContainer().getContainerSize();
    }

    @Override
    default boolean isEmpty() {
        return getContainer().isEmpty();
    }

    @Override
    default ItemStack getItem(int slot) {
        return getContainer().getItem(slot);
    }

    @Override
    default ItemStack removeItem(int slot, int amount) {
        return getContainer().removeItem(slot, amount);
    }

    @Override
    default ItemStack removeItemNoUpdate(int slot) {
        return getContainer().removeItemNoUpdate(slot);
    }

    @Override
    default void setItem(int slot, ItemStack stack) {
        getContainer().setItem(slot, stack);
    }

    @Override
    default void setChanged() {
        getContainer().setChanged();
    }

    @Override
    default boolean stillValid(Player player) {
        return getContainer().stillValid(player);
    }

    @Override
    default void clearContent() {
        getContainer().clearContent();
    }

    @Override
    default void startOpen(Player player) {
        if (player.isSpectator()) return;
        getOpenersCounter().incrementOpeners(player);
    }

    @Override
    default void stopOpen(Player player) {
        if (player.isSpectator()) return;
        getOpenersCounter().decrementOpeners(player);
    }

    @Override
    default int[] getSlotsForFace(Direction side) {
        // Inefficient...
        return IntStream.range(0, getContainerSize()).toArray();
    }

    @Override
    default boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        return true;
    }

    @Override
    default boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }
}
