package dev.denimred.picnicking.init.client;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.denimred.picnicking.init.PicnicEntityTypes;
import dev.denimred.picnicking.util.client.NoOpEntityRenderer;

public final class PicnicRenderers {
    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(PicnicEntityTypes.PICNIC_BLANKET, NoOpEntityRenderer::new);
    }
}
