package dev.denimred.picnicking.crown;

import dev.denimred.picnicking.init.PicnicItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Rarity;

// TODO: Add special effects to the crown :)
// TODO: Lock crown recipe behind advancement
// TODO: Improve crown armor texture?
public class CrownItem extends ArmorItem {
    public CrownItem() {
        this(ArmorMaterials.GOLD, PicnicItems.properties()
                .durability(ArmorMaterials.DIAMOND.getDurabilityForSlot(EquipmentSlot.HEAD))
                .rarity(Rarity.UNCOMMON));
    }

    public CrownItem(ArmorMaterial armorMaterial, Properties properties) {
        super(armorMaterial, EquipmentSlot.HEAD, properties);
    }
}
