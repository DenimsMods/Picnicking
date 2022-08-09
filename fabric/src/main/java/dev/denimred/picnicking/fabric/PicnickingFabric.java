package dev.denimred.picnicking.fabric;

import dev.denimred.picnicking.Picnicking;
import net.fabricmc.api.ModInitializer;

public class PicnickingFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Picnicking.init();
    }
}
