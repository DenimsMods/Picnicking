package dev.denimred.picnicking;

import dev.architectury.registry.CreativeTabRegistry;
import dev.denimred.picnicking.init.PicnicBlocks;
import dev.denimred.picnicking.init.PicnicItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class Picnicking {
    public static final String MOD_ID = "picnicking";

    public static final CreativeModeTab CREATIVE_TAB = CreativeTabRegistry.create(res("general"), () ->
            new ItemStack(PicnicBlocks.PICNIC_BLANKET.get()));
    
    public static void init() {
        PicnicBlocks.REGISTRY.register();
        PicnicItems.REGISTRY.register();
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
