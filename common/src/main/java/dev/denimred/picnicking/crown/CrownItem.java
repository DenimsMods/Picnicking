package dev.denimred.picnicking.crown;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.denimred.picnicking.crown.client.ClientCrownUtil;
import dev.denimred.picnicking.init.PicnicItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

// TODO: Add special effects to the crown :)
// TODO: Make crown advancement obtainable
// TODO: Improve crown armor texture?
@ParametersAreNonnullByDefault
public class CrownItem extends ArmorItem {
    public CrownItem() {
        this(ArmorMaterials.GOLD, PicnicItems.properties()
                .durability(ArmorMaterials.DIAMOND.getDurabilityForSlot(EquipmentSlot.HEAD))
                .rarity(Rarity.UNCOMMON));
    }

    public CrownItem(ArmorMaterial armorMaterial, Properties properties) {
        super(armorMaterial, EquipmentSlot.HEAD, properties);
    }

    @Override
    public String getDescriptionId() {
        boolean isKing = Platform.getEnvironment() != Env.CLIENT || ClientCrownUtil.isLocalPicnicKing();
        return isKing ? super.getDescriptionId() : super.getDescriptionId() + ".inert";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        tooltip.add(new TranslatableComponent(getDescriptionId(stack) + ".desc").withStyle(ChatFormatting.WHITE));
        super.appendHoverText(stack, level, tooltip, isAdvanced);
    }
}
