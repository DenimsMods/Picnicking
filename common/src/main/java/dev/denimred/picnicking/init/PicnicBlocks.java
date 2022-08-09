package dev.denimred.picnicking.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.denimred.picnicking.block.PicnicBlanketBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;
import java.util.function.Supplier;

import static dev.denimred.picnicking.Picnicking.MOD_ID;
import static net.minecraft.core.Registry.BLOCK_REGISTRY;

public final class PicnicBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(MOD_ID, BLOCK_REGISTRY);


    public static final RegistrySupplier<PicnicBlanketBlock> PICNIC_BLANKET = register("picnic_blanket", PicnicBlanketBlock::new);


    public static <T extends Block> RegistrySupplier<T> register(String name, Supplier<? extends T> sup) {
        return PicnicItems.registerBlockItem(registerNoItem(name, sup));
    }

    public static <T extends Block> RegistrySupplier<T> register(String name, Supplier<? extends T> sup, Function<T, BlockItem> blockItem) {
        return PicnicItems.registerBlockItem(registerNoItem(name, sup), blockItem);
    }

    public static <T extends Block> RegistrySupplier<T> registerNoItem(String name, Supplier<? extends T> sup) {
        return REGISTRY.register(name, sup);
    }
}
