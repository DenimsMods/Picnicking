package dev.denimred.picnicking.util.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.Player;

// This exists to combat Forge's picky illegal side classloading checker
// TODO: Do something better here
public final class ClientBits {
    public static RecipeBook getClientRecipeBook(Player player) {
        return ((LocalPlayer) player).getRecipeBook();
    }
}
