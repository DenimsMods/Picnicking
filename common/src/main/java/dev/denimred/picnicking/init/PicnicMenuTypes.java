package dev.denimred.picnicking.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.denimred.picnicking.basket.BasketMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

import static dev.denimred.picnicking.Picnicking.MOD_ID;
import static net.minecraft.core.Registry.MENU_REGISTRY;

public final class PicnicMenuTypes {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(MOD_ID, MENU_REGISTRY);


    public static final RegistrySupplier<MenuType<BasketMenu>> PICNIC_BASKET = register("picnic_basket", BasketMenu::new);


    public static <M extends AbstractContainerMenu> RegistrySupplier<MenuType<M>> register(String name, MenuType.MenuSupplier<M> sup) {
        return register(name, () -> new MenuType<>(sup));
    }

    public static <T extends MenuType<M>, M extends AbstractContainerMenu> RegistrySupplier<T> register(String name, Supplier<? extends T> sup) {
        return REGISTRY.register(name, sup);
    }
}
