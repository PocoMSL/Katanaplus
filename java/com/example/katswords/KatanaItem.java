
package com.example.katswords;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;

public class KatanaItem extends Item {
    private static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID BASE_ATTACK_SPEED_UUID  = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID BASE_ATTACK_RANGE_UUID  = UUID.fromString("33333333-3333-3333-3333-333333333333");

    public KatanaItem(Properties props) {
        super(props);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK; // Shield-like animation
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(EquipmentSlot slot) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        if (slot == EquipmentSlot.MAINHAND) {
            // Attack damage: Diamond sword is 7; +0.5 hearts (1.0 damage) -> 8.0 total
            builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Katana damage", 8.0 - 1.0, AttributeModifier.Operation.ADD_VALUE), slot);
            // Attack speed faster than diamond (-2.4). We'll use -2.0 (higher)
            builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Katana speed", -2.0, AttributeModifier.Operation.ADD_VALUE), slot);
            // +1 block attack range
            builder.add(ForgeMod.ATTACK_RANGE.get(), new AttributeModifier(BASE_ATTACK_RANGE_UUID, "Katana range", 1.0, AttributeModifier.Operation.ADD_VALUE), slot);
        }
        return builder.build();
    }
}
