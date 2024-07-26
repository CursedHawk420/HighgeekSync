package eu.highgeek.highgeeksync.common;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.objects.PlayerList;
import eu.highgeek.highgeeksync.objects.PlayerSettings;
import eu.highgeek.highgeeksync.sync.chat.ChannelManager;
import eu.highgeek.highgeeksync.sync.chat.DiscordUtil;
import eu.highgeek.highgeeksync.sync.inventory.InventoryManager;

public class Common {

    //public static List<PlayerSettings> playerSettings = new ArrayList<>();

    public static HashMap<String, PlayerSettings> playerSettings = new HashMap<>();

    public static void onPlayerJoin(Player player){

        InventoryManager.onPlayerJoin(player);

        PlayerSettings playerSetting = RedisManager.getPlayerSettings(player);
        playerSettings.put(player.getName(), playerSetting);
        
        //playerSettings.add(playerSetting);

        ChannelManager.onPlayerJoin(playerSetting, player);

        PlayerList.updatePlayerList();
    }

    public static void onFistJoin(Player player){

        DiscordUtil.generateLinkingCode(player);
    }
    public static void onPlayerComeback(Player player){
        
        DiscordUtil.loadLinkingCode(player);
    }


    public static void onPlayerQuit(Player player){
        InventoryManager.onPlayerLeave(player);
        playerSettings.remove(player.getName());
        DiscordUtil.codeMap.remove(player);

        ChannelManager.onPlayerQuit(player);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable() {
            @Override
            public void run() {
                PlayerList.updatePlayerList();
            }
        }, 20);
    }


}
