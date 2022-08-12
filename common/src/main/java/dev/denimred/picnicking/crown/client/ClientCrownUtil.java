package dev.denimred.picnicking.crown.client;

import net.minecraft.client.Minecraft;

import static dev.denimred.picnicking.crown.CrownUtil.PICNIC_KING_ADVANCEMENT;

public final class ClientCrownUtil {
    private static final Minecraft MC = Minecraft.getInstance();

    public static boolean isLocalPicnicKing() {
        return MC.player == null || MC.player.connection.getAdvancements().getAdvancements().get(PICNIC_KING_ADVANCEMENT) != null;
    }
}
