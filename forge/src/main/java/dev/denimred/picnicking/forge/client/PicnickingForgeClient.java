package dev.denimred.picnicking.forge.client;

import dev.denimred.picnicking.init.client.PicnicRenderers;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import static dev.denimred.picnicking.Picnicking.MOD_ID;
import static net.minecraftforge.api.distmarker.Dist.CLIENT;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

@EventBusSubscriber(modid = MOD_ID, value = CLIENT, bus = MOD)
public final class PicnickingForgeClient {
    @SubscribeEvent
    public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        PicnicRenderers.registerEntityRenderers();
    }

    @SubscribeEvent
    public static void onRegisterEntityLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        PicnicRenderers.registerModelLayers();
    }
}
