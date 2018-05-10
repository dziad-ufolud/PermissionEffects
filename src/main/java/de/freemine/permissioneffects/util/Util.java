package de.freemine.permissioneffects.util;


import de.freemine.permissioneffects.file.ConfigFile;
import de.freemine.permissioneffects.file.SettingsFile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static void addEffects(Player player) {
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.addPotionEffects(getEffectsToApply(player));
    }

    public static ArrayList<PotionEffect> getAllowedEffects(Player player) {
        ArrayList<PotionEffect> effects = new ArrayList<>();
        if (noBypass(player)) {
            for (PotionEffect effect : PotionEffect.values()) {
                if (player.hasPermission("pe." + effect.name().toLowerCase())) {
                    effects.add(effect);
                }
            }
        }
        return effects;
    }

    public static ArrayList<SimplePotionInfo> getApplicableEffects(Player player) {
        ArrayList<SimplePotionInfo> effects = new ArrayList<>();
        if (noBypass(player)) {
            for (PotionEffect effect : PotionEffect.values()) {
                if (hasApplyEffectPermission(player, effect) &&
                        SettingsFile.isEffectEnabled(player, effect)) {
                    List<Integer> amps = ConfigFile.getAmplifiers();
                    for (Integer integer : amps) {
                        if (hasApplyEffectPermission(player, effect, integer)) {
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
        for (SimplePotionInfo spi : getApplicableEffects(p)) {
            effects.add(spi.getBukkitEffect());
        }

        return effects;
    }

    public static void updateEffect(Player player, PotionEffect effect) {
        player.removePotionEffect(PotionEffectType.getByName(effect.name()));
        if (noBypass(player)) {
            if (hasApplyEffectPermission(player, effect) &&
                    SettingsFile.isEffectEnabled(player, effect)) {
                List<Integer> amps = ConfigFile.getAmplifiers();
                for (Integer integer : amps) {
                    if (hasApplyEffectPermission(player, effect, integer)) {
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

    public static boolean hasApplyEffectPermission(Player p, PotionEffect effect) {
        return p.hasPermission("pe." + effect.name().toLowerCase());
    }

    public static boolean hasApplyEffectPermission(Player p, PotionEffect effect, Integer amplifier) {
        return p.hasPermission("pe." + effect.name().toLowerCase() + "." + amplifier.toString());
    }

    public static boolean hasToggleEffectPermission(Player p, PotionEffect effect) {
        return p.hasPermission("pe." + effect.name().toLowerCase() + ".toggle");
    }

    public static boolean hasChangeOther(CommandSender sender) {
        return sender.hasPermission("pe.other") || sender.isOp();
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
