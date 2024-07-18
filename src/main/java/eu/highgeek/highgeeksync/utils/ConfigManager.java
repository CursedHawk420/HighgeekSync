package eu.highgeek.highgeeksync.utils;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import eu.highgeek.highgeeksync.Main;

public class ConfigManager {
    public static FileConfiguration config = null;
    public static FileConfiguration language = null;

    public static void reload() {
        File file = new File("plugins/HighgeekSync/config.yml");
        if(!file.exists()){
            Main.main.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
        File languageFile = new File("plugins/HighgeekSync/lang/" + getString("settings.language") + ".yml");
        if (!languageFile.exists()) {
            Main.main.saveResource("lang/en_EN.yml", false);
        }
        language = YamlConfiguration.loadConfiguration(languageFile);
    }

    public static String getString(String path) {
        if (path.contains("messages")) return language.getString(path.replace("messages.", ""));
        return config.getString(path);
    }

    public static String getColoredString(String path) {
        return getString(path).replaceAll("%prefix%", getString("messages.prefix")).replaceAll("&", "§").replaceAll("&n", "\n");
    }

    public static Boolean getBoolean(String path) {
        return config.getBoolean(path);
    }
}
