package dev.denimred.picnicking.forge.mixin;

import dev.denimred.picnicking.basket.BasketItem;
import dev.denimred.picnicking.basket.client.BasketItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.IItemRenderProperties;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@Mixin(BasketItem.class)
public final class BasketItemMixin extends Item {
    private BasketItemMixin(Properties arg) {
        super(arg);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return BasketItemRenderer.getInstance();
            }
        });
    }
}
