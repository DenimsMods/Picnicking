package dev.denimred.picnicking.init.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.denimred.picnicking.basket.client.BasketEntityRenderer;
import dev.denimred.picnicking.basket.client.BasketModel;
import dev.denimred.picnicking.crown.client.CrownModel;
import dev.denimred.picnicking.init.PicnicEntityTypes;
import dev.denimred.picnicking.util.client.NoOpEntityRenderer;

public final class PicnicRenderers {
    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(PicnicEntityTypes.PICNIC_BLANKET, NoOpEntityRenderer::new);
        EntityRendererRegistry.register(PicnicEntityTypes.PICNIC_BASKET, BasketEntityRenderer::new);
    }

    public static void registerModelLayers() {
        EntityModelLayerRegistry.register(CrownModel.MODEL_LAYER, CrownModel::createBodyLayer);
        EntityModelLayerRegistry.register(BasketModel.MODEL_LAYER, BasketModel::createBodyLayer);
    }
}
