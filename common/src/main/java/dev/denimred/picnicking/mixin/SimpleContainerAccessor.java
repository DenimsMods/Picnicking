package dev.denimred.picnicking.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleContainer.class)
public interface SimpleContainerAccessor {
    @Accessor("items") // Renamed due to stack overflow in BasketContainer because I'm stubborn
    NonNullList<ItemStack> accessItems();
}
