package dev.denimred.picnicking.util;

import com.google.gson.JsonObject;
import dev.denimred.picnicking.init.PicnicRecipes;
import dev.denimred.picnicking.mixin.CraftingContainerAccessor;
import dev.denimred.picnicking.mixin.CraftingMenuAccessor;
import dev.denimred.picnicking.mixin.InventoryMenuAccessor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class LockedShapedRecipe extends ShapedRecipe {
    private final ShapedRecipe wrapped;

    public LockedShapedRecipe(ShapedRecipe r) {
        super(r.getId(), r.getGroup(), r.getWidth(), r.getHeight(), r.getIngredients(), r.getResultItem());
        wrapped = r;
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        Player player = getPlayer(container);
        if (player == null) return false;
        RecipeBook recipeBook = getRecipeBook(level, player);
        return recipeBook.contains(getId()) && super.matches(container, level);
    }

    @Nullable
    public Player getPlayer(CraftingContainer container) {
        AbstractContainerMenu menu = ((CraftingContainerAccessor) container).getMenu();
        if (menu instanceof InventoryMenu iMenu) {
            return ((InventoryMenuAccessor) iMenu).getOwner();
        }
        if (menu instanceof CraftingMenu cMenu) {
            return ((CraftingMenuAccessor) cMenu).getPlayer();
        }
        return null;
    }

    public RecipeBook getRecipeBook(Level level, Player player) {
        return level.isClientSide
                ? ((LocalPlayer) player).getRecipeBook()
                : ((ServerPlayer) player).getRecipeBook();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PicnicRecipes.Serializers.CRAFTING_SHAPED_LOCKED.get();
    }

    // If we just implement RecipeSerializer then forge complains about the lack of IForgeRegistryEntry inheritance
    public static class Serializer extends SimpleRecipeSerializer<LockedShapedRecipe> {
        public Serializer() {
            super(unused -> {
                throw new AssertionError("This is impossible");
            });
        }

        @Override
        public LockedShapedRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            return new LockedShapedRecipe(SHAPED_RECIPE.fromJson(recipeId, serializedRecipe));
        }

        @Override
        public LockedShapedRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return new LockedShapedRecipe(SHAPED_RECIPE.fromNetwork(recipeId, buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, LockedShapedRecipe recipe) {
            SHAPED_RECIPE.toNetwork(buffer, recipe.wrapped);
        }
    }
}
