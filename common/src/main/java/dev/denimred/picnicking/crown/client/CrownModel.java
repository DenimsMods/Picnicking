package dev.denimred.picnicking.crown.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.ParametersAreNonnullByDefault;

import static dev.denimred.picnicking.Picnicking.res;

@ParametersAreNonnullByDefault
public class CrownModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation MODEL_LAYER = new ModelLayerLocation(res("picnic_king_crown"), "main");
    public static final ResourceLocation TEXTURE = res("textures/models/armor/picnic_king_crown.png");
    private static CrownModel instance = null;

    private CrownModel(ModelPart modelPart) {
        super(modelPart, RenderType::armorCutoutNoCull);
    }

    public static CrownModel getInstance() {
        if (instance == null) instance = new CrownModel(Minecraft.getInstance().getEntityModels().bakeLayer(MODEL_LAYER));
        return instance;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO).addOrReplaceChild("crown",
                CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 2.0F, 3.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, -9.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
        return LayerDefinition.create(mesh, 16, 8);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        setAllVisible(false);
        head.visible = true;
        super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
