package dev.denimred.picnicking.basket;

import dev.denimred.picnicking.init.PicnicEntityTypes;
import dev.denimred.picnicking.init.PicnicItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.context.UseOnContext;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.nbt.Tag.TAG_COMPOUND;
import static net.minecraft.nbt.Tag.TAG_LIST;
import static net.minecraft.world.entity.EntityType.ENTITY_TAG;
import static net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity.ITEMS_TAG;

@ParametersAreNonnullByDefault
public class BasketItem extends Item {
    public BasketItem() {
        this(PicnicItems.properties().stacksTo(1));
    }

    public BasketItem(Properties properties) {
        super(properties);
    }

    public static ItemStack createStack(BasketEntity basket) {
        ItemStack stack = PicnicItems.PICNIC_BASKET.get().getDefaultInstance();
        basket.saveWithoutId(stack.getOrCreateTagElement(ENTITY_TAG));
        if (basket.hasCustomName()) stack.setHoverName(basket.getCustomName());
        return stack;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return BasketEntity.place(context);
    }

    @Override
    public String getDescriptionId() {
        return PicnicEntityTypes.PICNIC_BASKET.get().getDescriptionId();
    }

    @Override
    public Component getName(ItemStack stack) {
        return super.getName(stack);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false; // Cannot fit inside shulkers, bundles, or other baskets
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        CompoundTag tag = stack.getTagElement(ENTITY_TAG);
        if (tag != null && tag.contains(ITEMS_TAG, TAG_LIST)) {
            ListTag list = tag.getList(ITEMS_TAG, TAG_COMPOUND);
            ItemUtils.onContainerDestroyed(itemEntity, list.stream().map(CompoundTag.class::cast).map(ItemStack::of));
        }
    }
}
