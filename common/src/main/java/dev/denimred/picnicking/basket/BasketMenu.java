package dev.denimred.picnicking.basket;

import dev.denimred.picnicking.init.PicnicMenuTypes;
import dev.denimred.picnicking.util.SimpleContainerMenu;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BasketMenu extends SimpleContainerMenu {
    public static final int BASKET_ROWS = 2;
    public static final int BASKET_COLUMNS = 5;
    public static final int BASKET_LEFT = 44;
    public static final int BASKET_TOP = 18;

    public static final int PLAYER_ROWS = 3;
    public static final int PLAYER_COLUMNS = 9;
    public static final int PLAYER_INV_LEFT = 8;
    public static final int PLAYER_INV_TOP = 68;
    public static final int PLAYER_HOTBAR_TOP = 126;

    public static final int SLOTS_SPACING = 2;

    public BasketMenu(int id, Inventory inventory) {
        this(id, inventory, new BasketContainer(BasketContainer.BASKET_SIZE));
    }

    public BasketMenu(int id, Inventory inventory, Container container) {
        this(PicnicMenuTypes.PICNIC_BASKET.get(), id, inventory, container);
    }

    public BasketMenu(@Nullable MenuType<?> menuType, int id, Inventory inventory, Container container) {
        super(menuType, id, container);
        container.startOpen(inventory.player);
        // Add basket slots
        for (int y = 0; y < BASKET_ROWS; y++) {
            for (int x = 0; x < BASKET_COLUMNS; x++) {
                int index = x + y * BASKET_COLUMNS;
                int spacing = 16 + SLOTS_SPACING;
                int xPos = BASKET_LEFT + x * spacing;
                int yPos = BASKET_TOP + y * spacing;
                addSlot(new Slot(container, index, xPos, yPos) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return stack.getItem().canFitInsideContainerItems();
                    }
                });
            }
        }
        // Add player inventory slots
        for (int y = 0; y < PLAYER_ROWS; y++) {
            for (int x = 0; x < PLAYER_COLUMNS; x++) {
                int index = x + y * PLAYER_COLUMNS + PLAYER_COLUMNS;
                int spacing = 16 + SLOTS_SPACING;
                int xPos = PLAYER_INV_LEFT + x * spacing;
                int yPos = PLAYER_INV_TOP + y * spacing;
                addSlot(new Slot(inventory, index, xPos, yPos));
            }
        }
        // Add player hotbar slots
        for (int i = 0; i < PLAYER_COLUMNS; i++) {
            int spacing = 16 + SLOTS_SPACING;
            int xPos = PLAYER_INV_LEFT + i * spacing;
            addSlot(new Slot(inventory, i, xPos, PLAYER_HOTBAR_TOP));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // TODO: This was copied; clean up and refactor as needed
        ItemStack oldStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack newStack = slot.getItem();
            oldStack = newStack.copy();
            if (index < BASKET_ROWS * 9) {
                if (!moveItemStackTo(newStack, BASKET_ROWS * 9, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(newStack, 0, BASKET_ROWS * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (newStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return oldStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
