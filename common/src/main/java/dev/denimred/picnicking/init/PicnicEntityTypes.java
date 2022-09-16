package dev.denimred.picnicking.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.denimred.picnicking.basket.BasketEntity;
import dev.denimred.picnicking.blanket.BlanketEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

import static dev.denimred.picnicking.Picnicking.MOD_ID;
import static net.minecraft.SharedConstants.CHECK_DATA_FIXER_SCHEMA;
import static net.minecraft.core.Registry.ENTITY_TYPE_REGISTRY;

public final class PicnicEntityTypes {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(MOD_ID, ENTITY_TYPE_REGISTRY);


    public static final RegistrySupplier<EntityType<BlanketEntity>> PICNIC_BLANKET = register("picnic_blanket", BlanketEntity::createType);
    public static final RegistrySupplier<EntityType<BasketEntity>> PICNIC_BASKET = register("picnic_basket", BasketEntity::createType);


    public static <E extends Entity, B extends EntityType.Builder<E>> RegistrySupplier<EntityType<E>> register(String name, Supplier<? extends B> sup) {
        return REGISTRY.register(name, () -> buildSafely(name, sup));
    }

    public static <E extends Entity, B extends EntityType.Builder<E>> EntityType<E> buildSafely(String name, Supplier<? extends B> sup) {
        if (!CHECK_DATA_FIXER_SCHEMA) return sup.get().build(name);
        try {
            CHECK_DATA_FIXER_SCHEMA = false;
            return sup.get().build(name);
        } finally {
            CHECK_DATA_FIXER_SCHEMA = true;
        }
    }
}
