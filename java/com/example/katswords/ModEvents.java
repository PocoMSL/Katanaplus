
package com.example.katswords;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ModEvents {
    public static void register() {
        MinecraftForge.EVENT_BUS.register(new ModEvents());
    }

    private static boolean isDisallowedToBlock(DamageSource src) {
        // Don't block from: lava, potions/magic, taser (Superb Warfare), explosions (vanilla & mods)
        ResourceLocation key = src.typeHolder().unwrapKey().map(k -> k.location()).orElse(new ResourceLocation("minecraft","unknown"));
        String ns = key.getNamespace();
        String path = key.getPath();
        boolean explosion = src.isExplosion();
        boolean lava = path.contains("lava");
        boolean magic = path.contains("magic") || path.contains("potion");
        boolean taser = ns.contains("superb") && (path.contains("taser") || path.contains("electric"));
        boolean gunExpl = (ns.contains("superb") || ns.contains("tacz") || ns.contains("timeless")) && (path.contains("explosion") || path.contains("grenade") || path.contains("rocket"));
        return explosion || lava || magic || taser || gunExpl;
    }

    @SubscribeEvent
    public void onShieldBlock(ShieldBlockEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        ItemStack stack = player.getUseItem();
        if (stack.getItem() == ModItems.KATANA.get()) {
            // Reduce incoming damage by 75% (take only 25%)
            e.setBlockedDamage(e.getBlockedDamage() * 0.75f);
            // Make the katana take 4x less durability when blocking
            int dmg = Math.max(1, (int)Math.floor(e.getShieldDamage() / 4.0));
            e.setShieldDamage(dmg);
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        ItemStack stack = player.getUseItem();
        if (stack.getItem() == ModItems.KATANA.get() && player.isUsingItem()) {
            // Allow blocking from any direction; cancel/scale unless it's a disallowed source
            if (!isDisallowedToBlock(e.getSource())) {
                e.setAmount(e.getAmount() * 0.25f); // 75% reduction
            }
        }
    }

    // Prevent axes from disabling "shield" (katana)
    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (player.isUsingItem() && player.getUseItem().getItem() == ModItems.KATANA.get()) {
            DamageSource src = e.getSource();
            // If attacker uses axe, do not disable use - vanilla handles shield disable elsewhere;
            // here we simply ensure we keep using the item by restarting the use.
            if (src.getEntity() != null && src.getEntity().getMainHandItem().getItem().toString().contains("axe")) {
                player.startUsingItem(player.getUsedItemHand());
            }
        }
    }
}
