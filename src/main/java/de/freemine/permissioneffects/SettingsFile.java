package de.freemine.permissioneffects;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

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

    public static void setEffect(OfflinePlayer p, PotionEffect effect, Boolean enabled) {
        ConfigurationSection sect = getPlayerSection(p);
        // only store when disabled so the db remains smaller
        if (!enabled) {
            sect.set(effect.name().toLowerCase(), enabled);
        } else {
            sect.set(effect.name().toLowerCase(), null);
        }
    }

    public static boolean isEffectEnabled(OfflinePlayer p, PotionEffect effect) {
        return getPlayerSection(p).getBoolean(effect.name().toLowerCase(), true);
    }

    public static ConfigurationSection getPlayerSection(OfflinePlayer p) {
        ConfigurationSection sect = config.getConfigurationSection("PE." + p.getUniqueId().toString());
        if (sect == null) {
            sect = config.createSection("PE." + p.getUniqueId().toString());
        }
        return sect;
    }
}
