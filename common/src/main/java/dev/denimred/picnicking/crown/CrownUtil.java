package dev.denimred.picnicking.crown;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import static dev.denimred.picnicking.Picnicking.res;

public final class CrownUtil {
    public static final ResourceLocation PICNIC_KING_ADVANCEMENT = res("root");

    public static boolean isPicnicKing(ServerPlayer player) {
        Advancement advancement = player.server.getAdvancements().getAdvancement(PICNIC_KING_ADVANCEMENT);
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }
}
