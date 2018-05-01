package de.freemine.permissioneffects;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;

public class SettingsFile {

    private static YamlConfiguration config;

    private static File getRealFile() {
        return new File(Reference.plugin.getDataFolder(), "data.yml");
    }

    public static void init() {
        SettingsFile.config = YamlConfiguration.loadConfiguration(getRealFile());
    }

    public static void save() {
        try {
            SettingsFile.config.save(getRealFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setEffect(OfflinePlayer p, PotionEffectType effect, Boolean enabled) {
        ConfigurationSection sect = getPlayerSection(p);
        // only store when disabled so the db remains smaller
        if (!enabled) {
            sect.set(effect.getName().toLowerCase(), enabled);
        } else {
            sect.set(effect.getName().toLowerCase(), null);
        }
    }

    public static boolean isEffectEnabled(OfflinePlayer p, PotionEffect effect) {
        return getPlayerSection(p).getBoolean(effect.name().toLowerCase(), true);
    }

    public static ConfigurationSection getPlayerSection(OfflinePlayer p) {
        return config.getConfigurationSection("PE." + p.getUniqueId().toString());
    }
}
