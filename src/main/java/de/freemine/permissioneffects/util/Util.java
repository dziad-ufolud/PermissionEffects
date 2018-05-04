package de.freemine.permissioneffects.util;


import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class Util {
    public static void addEffects(Player player) {
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.addPotionEffects(getEffectsToApply(player));
    }

    public static ArrayList<PotionEffect> getAllowedEffects(Player player) {
        ArrayList<PotionEffect> effects = new ArrayList<>();
        if (!player.hasPermission("pe.bypass") || !player.isOp()) {
            for (PotionEffect effect : PotionEffect.values()) {
                if (player.hasPermission("pe." + effect.name().toLowerCase())) {
                    effects.add(effect);
                }
            }
        }
        return effects;
    }

    public static ArrayList<SimplePotionInfo> getAplicableEffects(Player player) {
        ArrayList<SimplePotionInfo> effects = new ArrayList<>();
        if (noBypass(player)) {
            for (PotionEffect effect : PotionEffect.values()) {
                if (player.hasPermission("pe." + effect.name().toLowerCase()) &&
                        SettingsFile.isEffectEnabled(player, effect)) {
                    for (Integer integer : new int[]{4, 3, 2, 1}) {//TODO replace with config option for amplifiers
                        if (player.hasPermission("pe." + effect.name().toLowerCase() + "." + integer.toString())) {
                            effects.add(new SimplePotionInfo(effect, integer));
                            break;
                        }
                    }
                }
            }
        }
        return effects;
    }

    public static ArrayList<org.bukkit.potion.PotionEffect> getEffectsToApply(Player p) {
        ArrayList<org.bukkit.potion.PotionEffect> effects = new ArrayList<>();
        for (SimplePotionInfo spi : getAplicableEffects(p)) {
            effects.add(spi.getBukkitEffect());
        }

        return effects;
    }

    public static void updateEffect(Player player, PotionEffect effect) {
        player.removePotionEffect(PotionEffectType.getByName(effect.name()));
        if (noBypass(player)) {
            if (player.hasPermission("pe." + effect.name().toLowerCase()) &&
                    SettingsFile.isEffectEnabled(player, effect)) {
                for (Integer integer : new int[]{4, 3, 2, 1}) {//TODO replace with config option for amplifiers
                    if (player.hasPermission("pe." + effect.name().toLowerCase() + "." + integer.toString())) {
                        player.addPotionEffect(new SimplePotionInfo(effect, integer).getBukkitEffect());
                        break;
                    }
                }
            }
        }
    }

    public static boolean noBypass(Player player) {
        return !player.hasPermission("pe.bypass") || !player.isOp();
    }

    public static class SimplePotionInfo {
        public final PotionEffect effect;
        public final Integer amp;

        public SimplePotionInfo(PotionEffect effect, Integer amp) {
            this.effect = effect;
            this.amp = amp;
        }

        public final org.bukkit.potion.PotionEffect getBukkitEffect() {
            return new org.bukkit.potion.PotionEffect(PotionEffectType.getByName(this.effect.name()),
                    100000000,
                    this.amp - 1,
                    false,
                    false);
        }

    }


}
