package dev.denimred.picnicking.util;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleContainerMenu extends AbstractContainerMenu {
    public final Container container;

    protected SimpleContainerMenu(@Nullable MenuType<?> menuType, int i, Container container) {
        super(menuType, i);
        this.container = container;
    }
}
