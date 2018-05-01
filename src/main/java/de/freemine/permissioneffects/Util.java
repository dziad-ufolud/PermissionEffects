package de.freemine.permissioneffects;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Util {
    public static void addEffects(Player player) {
        PotionEffectType[] possibleEffects = PotionEffectType.values();


        // highest first so the loop applies it and then quits
        Integer[] strength = {4, 3, 2, 1};

        if (!player.hasPermission("pe.bypass") || !player.isOp()) {
            for (PotionEffectType effect : possibleEffects) {
                if (player.hasPermission("pe." + effect.getName().toLowerCase()) && SettingsFile.isEffectEnabled(player, effect)) {
                    for (Integer integer : strength) {
                        if (player.hasPermission("pe." + effect.getName().toLowerCase() + "." + integer.toString())) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(effect.getName()), 100000000, integer - 1, false, false));
                            break;
                        }
                    }
                    //Commented out so because it applies it already in the Integer loop
                    //player.addPotionEffect(new PotionEffect(PotionEffectType.getByName(effect.getName()), 1000000000, 0, false, false));
                }
            }
        }
    }
}
