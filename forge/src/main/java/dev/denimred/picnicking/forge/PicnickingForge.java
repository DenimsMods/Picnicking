package dev.denimred.picnicking.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.denimred.picnicking.Picnicking;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static dev.denimred.picnicking.Picnicking.MOD_ID;

@Mod(MOD_ID)
public class PicnickingForge {
    public PicnickingForge() {
        EventBuses.registerModEventBus(MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Picnicking.init();
    }
}
