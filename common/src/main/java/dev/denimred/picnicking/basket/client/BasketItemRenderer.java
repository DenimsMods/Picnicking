package dev.denimred.picnicking.basket.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import dev.denimred.picnicking.basket.BasketEntity;
import dev.denimred.picnicking.init.PicnicEntityTypes;
import dev.denimred.picnicking.mixin.client.EntityRenderDispatcherAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BasketItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static BasketItemRenderer instance;
    private final BasketEntity basket;

    public BasketItemRenderer() {
        this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public BasketItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
        basket = PicnicEntityTypes.PICNIC_BASKET.get().create(dispatcher.level);
    }

    public static BlockEntityWithoutLevelRenderer getInstance() {
        if (instance == null) instance = new BasketItemRenderer();
        return instance;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transform, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        // This feels stupidly inefficient, but I won't care until it starts affecting performance
        basket.loadFromItem(stack);
        renderEntityAsItem(basket, poseStack, buffers, light);
    }

    public static void renderEntityAsItem(Entity basket, PoseStack stack, MultiBufferSource buffers, int light) {
        Quaternion quaternion2 = Vector3f.XP.rotationDegrees(20.0F);
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        quaternion2.conj();
        dispatcher.overrideCameraOrientation(quaternion2);
        boolean shouldRenderShadow = ((EntityRenderDispatcherAccessor) dispatcher).isShouldRenderShadow();
        dispatcher.setRenderShadow(false);
        boolean shouldRenderHitBoxes = dispatcher.shouldRenderHitBoxes();
        dispatcher.setRenderHitBoxes(false);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        dispatcher.render(basket, 0.5, 0.0, 0.5, 0.0F, 1.0F, stack, buffers, light);
        bufferSource.endBatch();
        dispatcher.setRenderShadow(shouldRenderShadow);
        dispatcher.setRenderHitBoxes(shouldRenderHitBoxes);
    }
}
