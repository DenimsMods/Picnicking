package dev.denimred.picnicking.thermos;

import com.mojang.datafixers.util.Pair;
import dev.denimred.picnicking.init.PicnicItems;
import dev.denimred.picnicking.init.PicnicRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class ThermosFillingRecipe extends CustomRecipe {
    public ThermosFillingRecipe(ResourceLocation id) {
        super(id);
    }

    private static ThermosItem asThermos(ItemStack stack) {
        return (ThermosItem) stack.getItem();
    }

    public boolean[] getUnusedSlots(CraftingContainer container) {
        boolean[] unusedSlots = new boolean[container.getContainerSize()];
        int charge = -1;
        int max = -1;
        // Find the thermos and add to the charge
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            Item item = stack.getItem();
            if (item instanceof ThermosItem thermos) {
                charge = thermos.getCharge(stack);
                max = thermos.getMaxCharge(stack);
                break;
            }
        }
        // Find the unused slots
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            // Ignore thermos and air
            if (stack.isEmpty() || stack.getItem() instanceof ThermosItem) continue;
            // Increment charge until max is hit, then start listing as unused
            if (charge < max && stack.is(PicnicItems.Tags.THERMOS_DRINKABLES)) {
                charge++;
            } else unusedSlots[i] = true;
        }
        return unusedSlots;
    }

    @Nullable
    public Pair<ItemStack, List<ItemStack>> getCraftingComponents(CraftingContainer container) {
        ItemStack thermos = ItemStack.EMPTY;
        List<ItemStack> drinks = Lists.newArrayList();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            // Ignore air
            if (stack.isEmpty()) continue;
            boolean isDrinkable = stack.is(PicnicItems.Tags.THERMOS_DRINKABLES);
            boolean isThermos = stack.getItem() instanceof ThermosItem;
            // Can only contain drinkable or thermos items
            if (!isDrinkable && !isThermos) return null;
            // Only one thermos is allowed
            if (isThermos && !thermos.isEmpty()) return null;
            // The thermos must not be full
            if (isThermos && asThermos(stack).isFullyCharged(stack)) return null;
            // All drinkables must be the same
            if (isDrinkable && !drinks.isEmpty() && !drinks.get(0).sameItem(stack)) return null;
            // Drinkable much match thermos type
            if (isDrinkable && !thermos.isEmpty()) {
                ThermosItem thermosItem = asThermos(thermos);
                ItemStack drink = thermosItem.getDrink(thermos);
                if (!drink.isEmpty() && !drink.sameItem(stack)) return null;
            } else if (isThermos && !drinks.isEmpty()) {
                ThermosItem thermosItem = asThermos(stack);
                ItemStack drink = thermosItem.getDrink(stack);
                if (!drink.isEmpty() && !drink.sameItem(drinks.get(0))) return null;
            }
            // Assign thermos/drinks
            if (isThermos) thermos = stack;
            if (isDrinkable) drinks.add(stack);
        }
        // Ensure a valid state just in case
        if (thermos.isEmpty() || drinks.isEmpty()) return null;
        return Pair.of(thermos, drinks);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return getCraftingComponents(container) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        Pair<ItemStack, List<ItemStack>> components = getCraftingComponents(container);
        if (components == null) return ItemStack.EMPTY;

        ItemStack thermos = components.getFirst();
        ThermosItem thermosItem = (ThermosItem) thermos.getItem();
        ItemStack outputThermos = thermos.copy();
        List<ItemStack> drinkables = components.getSecond();
        int newCharge = thermosItem.getCharge(thermos) + drinkables.size();

        return thermosItem.fillWithDrink(outputThermos, drinkables.get(0), newCharge);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remainders = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        boolean[] unusedSlots = getUnusedSlots(container);

        for (int i = 0; i < remainders.size(); ++i) {
            ItemStack stack = container.getItem(i);
            if (unusedSlots[i]) {
                // Have to make a copy here since the original stack gets decremented
                remainders.set(i, stack.copy());
            } else if (stack.getItem().hasCraftingRemainingItem()) {
                remainders.set(i, new ItemStack(stack.getItem().getCraftingRemainingItem()));
            } else if (stack.is(PicnicItems.Tags.WOODEN_BOWL_REMAINDERS)) {
                remainders.set(i, new ItemStack(Items.BOWL));
            } else if (stack.is(PicnicItems.Tags.GLASS_BOTTLE_REMAINDERS)) {
                remainders.set(i, new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return remainders;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PicnicRecipes.Serializers.THERMOS_FILLING.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return PicnicItems.THERMOS.get().getDefaultInstance();
    }
}
