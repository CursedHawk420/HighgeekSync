package eu.highgeek.highgeeksync.common;

import java.util.HashMap;

import org.bukkit.entity.Player;

import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.objects.PlayerSettings;
import eu.highgeek.highgeeksync.sync.chat.ChannelManager;
import eu.highgeek.highgeeksync.sync.inventory.InventoryManager;

public class Common {

    //public static List<PlayerSettings> playerSettings = new ArrayList<>();

    public static HashMap<Player, PlayerSettings> playerSettings = new HashMap<>();

    public static void onPlayerJoin(Player player){

        InventoryManager.onPlayerJoin(player);

        PlayerSettings playerSetting = RedisManager.getPlayerSettings(player);
        playerSettings.put(player, playerSetting);
        
        //playerSettings.add(playerSetting);

        ChannelManager.onPlayerJoin(playerSetting, player);
    }

    public static void onPlayerQuit(Player player){
        InventoryManager.onPlayerLeave(player);
        playerSettings.remove(player);

        ChannelManager.onPlayerQuit(player);
    }


}
