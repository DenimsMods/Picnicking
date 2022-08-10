package dev.denimred.picnicking.util.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class NoOpEntityRenderer<T extends net.minecraft.world.entity.Entity> extends EntityRenderer<T> {
    public NoOpEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.0f;
        shadowStrength = 0.0f;
    }

    @Override
    public void render(T entity, float entityYaw, float deltaTicks, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        // no-op
    }

    @Override
    public boolean shouldRender(T entity, Frustum frustum, double camX, double camY, double camZ) {
        return entityRenderDispatcher.shouldRenderHitBoxes() && super.shouldRender(entity, frustum, camX, camY, camZ);
    }

    @Override
    protected boolean shouldShowName(T entity) {
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TextureManager.INTENTIONAL_MISSING_TEXTURE;
    }
}
