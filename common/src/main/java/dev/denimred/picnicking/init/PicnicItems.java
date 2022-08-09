package dev.denimred.picnicking.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.denimred.picnicking.item.ThermosItem;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;
import java.util.function.Supplier;

import static dev.denimred.picnicking.Picnicking.*;
import static net.minecraft.core.Registry.ITEM_REGISTRY;

public final class PicnicItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(MOD_ID, ITEM_REGISTRY);


    public static final RegistrySupplier<ThermosItem> THERMOS = register("thermos", ThermosItem::new);


    public static <T extends Item> RegistrySupplier<T> register(String name, Supplier<? extends T> sup) {
        return REGISTRY.register(name, sup);
    }

    public static <T extends Block> RegistrySupplier<T> registerBlockItem(RegistrySupplier<T> sup) {
        return registerBlockItem(sup, block -> new BlockItem(block, properties()));
    }

    public static <T extends Block> RegistrySupplier<T> registerBlockItem(RegistrySupplier<T> sup, Function<T, BlockItem> factory) {
        register(sup.getId().getPath(), () -> factory.apply(sup.get()));
        return sup;
    }

    public static Item.Properties properties() {
        return new Item.Properties().tab(CREATIVE_TAB);
    }


    public static final class Tags {
        public static final TagKey<Item> THERMOS_DRINKABLES = create("thermos_drinkables");


        public static TagKey<Item> create(String name) {
            return TagKey.create(ITEM_REGISTRY, res(name));
        }
    }
}
