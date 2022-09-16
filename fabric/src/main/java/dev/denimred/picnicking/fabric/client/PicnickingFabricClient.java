package dev.denimred.picnicking.fabric.client;

import dev.denimred.picnicking.basket.client.BasketItemRenderer;
import dev.denimred.picnicking.crown.client.CrownModel;
import dev.denimred.picnicking.init.PicnicItems;
import dev.denimred.picnicking.init.client.PicnicRenderers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

public final class PicnickingFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PicnicRenderers.registerEntityRenderers();
        PicnicRenderers.registerModelLayers();
        registerCrownArmorRenderer();
        // Can't put this in PicnicRenderers since it's Fabric-specific
        BuiltinItemRendererRegistry.INSTANCE.register(PicnicItems.PICNIC_BASKET.get(), BasketItemRenderer.getInstance()::renderByItem);
    }

    private static void registerCrownArmorRenderer() {
        ArmorRenderer.register((matrices, vertexConsumers, stack, entity, slot, light, contextModel) -> {
            var model = CrownModel.getInstance();
            contextModel.copyPropertiesTo(model);
            ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, model, CrownModel.TEXTURE);
        }, PicnicItems.PICNIC_KING_CROWN.get());
    }
}
