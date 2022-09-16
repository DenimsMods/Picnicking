package dev.denimred.picnicking.basket.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.denimred.picnicking.Picnicking;
import dev.denimred.picnicking.basket.BasketMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BasketScreen extends AbstractContainerScreen<BasketMenu> {
    private static final ResourceLocation BACKGROUND = Picnicking.res("textures/gui/container/basket.png");

    public BasketScreen(BasketMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        passEvents = false;
        imageWidth = 176;
        imageHeight = 150;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTick);
        renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack stack, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        blit(stack, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
