package dev.denimred.picnicking;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.utils.Env;
import dev.denimred.picnicking.basket.client.BasketScreen;
import dev.denimred.picnicking.init.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class Picnicking {
    public static final String MOD_ID = "picnicking";
    public static final CreativeModeTab CREATIVE_TAB = CreativeTabRegistry.create(res("general"), () -> new ItemStack(PicnicItems.PICNIC_BASKET.get()));

    public static void init() {
        PicnicBlocks.REGISTRY.register();
        PicnicItems.REGISTRY.register();
        PicnicEntityTypes.REGISTRY.register();
        PicnicMenuTypes.REGISTRY.register();
        PicnicRecipes.Serializers.REGISTRY.register();

        // TODO: This is stupid
        if (Platform.getEnvironment() == Env.CLIENT) {
            ClientLifecycleEvent.CLIENT_SETUP.register(mc -> MenuRegistry.registerScreenFactory(PicnicMenuTypes.PICNIC_BASKET.get(), BasketScreen::new));
        }
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
