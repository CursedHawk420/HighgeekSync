package eu.highgeek.highgeeksync.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderParser {


    public static String parsePlaceholders(String input, Player player){
        return PlaceholderAPI.setPlaceholders(player, input);
    }
}
