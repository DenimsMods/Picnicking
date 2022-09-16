package dev.denimred.picnicking.basket.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import dev.denimred.picnicking.Picnicking;
import dev.denimred.picnicking.basket.BasketEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BasketEntityRenderer extends EntityRenderer<BasketEntity> {
    private static final ResourceLocation TEXTURE = Picnicking.res("textures/entity/picnic_basket.png");
    private final BasketModel<BasketEntity> model;

    public BasketEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new BasketModel<>(context.bakeLayer(BasketModel.MODEL_LAYER));
    }

    @Override
    public void render(BasketEntity basket, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0, 1.5, 0.0);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - entityYaw));

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        model.setupAnim(basket, 0.0F, 0.0F, partialTicks, 0.0F, 0.0F);
        VertexConsumer buffer = buffers.getBuffer(model.renderType(getTextureLocation(basket)));
        model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
        super.render(basket, entityYaw, partialTicks, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BasketEntity entity) {
        return TEXTURE;
    }
}
