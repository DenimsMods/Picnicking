package dev.denimred.picnicking.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.denimred.picnicking.thermos.ThermosFillingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;

import java.util.function.Supplier;

import static dev.denimred.picnicking.Picnicking.MOD_ID;
import static net.minecraft.core.Registry.RECIPE_SERIALIZER_REGISTRY;

public final class PicnicRecipes {
    public static final class Serializers {
        public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(MOD_ID, RECIPE_SERIALIZER_REGISTRY);


        public static final RegistrySupplier<SimpleRecipeSerializer<ThermosFillingRecipe>> THERMOS_FILLING =
                register("crafting_special_thermosfilling", () -> new SimpleRecipeSerializer<>(ThermosFillingRecipe::new));


        public static <R extends Recipe<?>, T extends RecipeSerializer<R>> RegistrySupplier<T> register(String name, Supplier<? extends T> sup) {
            return REGISTRY.register(name, sup);
        }
    }
}
