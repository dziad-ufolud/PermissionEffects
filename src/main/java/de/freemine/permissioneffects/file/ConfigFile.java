package de.freemine.permissioneffects.file;

import de.freemine.permissioneffects.Reference;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

public class ConfigFile {

    private static FileConfiguration config;

    private static FileConfiguration getRealFile() {
        return Reference.plugin.getConfig();
    }

    public static void init() {
        ConfigFile.config = getRealFile();
        if (getAmplifiers().size() == 0) {
            config.set("PE.amplifiers", Arrays.asList(4, 3, 2, 1));
        }
    }

    public static List<Integer> getAmplifiers() {
        return config.getIntegerList("PE.amplifiers");
    }


}
