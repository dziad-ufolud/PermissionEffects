package de.freemine.permissioneffects;


import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class Util {
    public static void addEffects(Player player) {


        // highest first so the loop applies it and then quits
        Integer[] strength = {4, 3, 2, 1};

        if (!player.hasPermission("pe.bypass") || !player.isOp()) {
            for (PotionEffect effect : de.freemine.permissioneffects.PotionEffect.values()) {
                if (player.hasPermission("pe." + effect.name().toLowerCase()) && SettingsFile.isEffectEnabled(player, effect)) {
                    for (Integer integer : strength) {
                        if (player.hasPermission("pe." + effect.name().toLowerCase() + "." + integer.toString())) {
                            player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.getByName(effect.name()), 100000000, integer - 1, false, false));
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
