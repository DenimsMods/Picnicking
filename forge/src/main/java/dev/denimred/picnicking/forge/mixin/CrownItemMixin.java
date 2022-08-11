package dev.denimred.picnicking.forge.mixin;

import dev.denimred.picnicking.crown.CrownItem;
import dev.denimred.picnicking.crown.client.CrownModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Consumer;

@Mixin(CrownItem.class)
public final class CrownItemMixin extends ArmorItem {
    private CrownItemMixin(ArmorMaterial arg, EquipmentSlot arg2, Properties arg3) {
        super(arg, arg2, arg3);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public HumanoidModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
                return CrownModel.getInstance();
            }
        });
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return CrownModel.TEXTURE.toString();
    }
}
