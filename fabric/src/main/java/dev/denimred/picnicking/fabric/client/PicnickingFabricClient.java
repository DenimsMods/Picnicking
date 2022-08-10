package dev.denimred.picnicking.fabric.client;

import dev.denimred.picnicking.init.client.PicnicRenderers;
import net.fabricmc.api.ClientModInitializer;

public final class PicnickingFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PicnicRenderers.registerEntityRenderers();
    }
}
