package dev.denimred.picnicking.basket.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.denimred.picnicking.basket.BasketEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

import static dev.denimred.picnicking.Picnicking.res;

@ParametersAreNonnullByDefault
public class BasketModel<E extends BasketEntity> extends EntityModel<E> {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(res("picnic_basket"), "main");
    public static final ResourceLocation TEXTURE = res("textures/entity/picnic_basket.png");
    protected final ModelPart base;
    protected final ModelPart backLid;
    protected final ModelPart frontLid;
    protected final ModelPart backHandle;
    protected final ModelPart frontHandle;

    public BasketModel(ModelPart root) {
        this(root, RenderType::entityCutout);
    }

    public BasketModel(ModelPart root, Function<ResourceLocation, RenderType> renderType) {
        super(renderType);
        base = root.getChild("base");
        backLid = base.getChild("back_lid");
        frontLid = base.getChild("front_lid");
        backHandle = base.getChild("back_handle");
        frontHandle = base.getChild("front_handle");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition base = root.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -7.0F, -7.0F, 10.0F, 7.0F, 14.0F), PartPose.offset(0.0F, 24.0F, 0.0F));

        base.addOrReplaceChild("back_lid", CubeListBuilder.create().texOffs(0, 29).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 1.0F, 7.0F), PartPose.offset(0.0F, -8.0F, 0.0F));

        base.addOrReplaceChild("front_lid", CubeListBuilder.create().texOffs(0, 21).addBox(-5.0F, 0.0F, -7.0F, 10.0F, 1.0F, 7.0F), PartPose.offset(0.0F, -8.0F, 0.0F));

        base.addOrReplaceChild("back_handle", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(5.0F, -7.5F, -0.5F, 1.0F, 8.0F, 1.0F).mirror(false)
                .texOffs(0, 0).addBox(-6.0F, -7.5F, -0.5F, 1.0F, 8.0F, 1.0F)
                .texOffs(34, 0).addBox(-5.0F, -7.5F, -0.5F, 10.0F, 1.0F, 1.0F), PartPose.offset(0.0F, -6.5F, 1.5F));

        base.addOrReplaceChild("front_handle", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(5.0F, -7.5F, -0.5F, 1.0F, 8.0F, 1.0F).mirror(false)
                .texOffs(0, 0).addBox(-6.0F, -7.5F, -0.5F, 1.0F, 8.0F, 1.0F)
                .texOffs(34, 0).mirror().addBox(-5.0F, -7.5F, -0.5F, 10.0F, 1.0F, 1.0F).mirror(false), PartPose.offset(0.0F, -6.5F, -1.5F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(E basket, float limbSwing, float limbSwingAmount, float partialTicks, float netHeadYaw, float headPitch) {
        frontLid.xRot = -basket.parts.frontLid.getAngle(partialTicks);
        backLid.xRot = basket.parts.backLid.getAngle(partialTicks);
        frontHandle.xRot = -basket.parts.handles.getAngle(partialTicks);
        backHandle.xRot = -frontHandle.xRot;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        base.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
