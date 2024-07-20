package eu.highgeek.highgeeksync.common;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.objects.PlayerSettings;
import eu.highgeek.highgeeksync.sync.chat.ChannelManager;

public class Common {

    public static List<PlayerSettings> playerSettings = new ArrayList<>();

    public static void onPlayerJoin(Player player){
        PlayerSettings playerSetting = RedisManager.getPlayerSettings(player);
        playerSettings.add(playerSetting);

        ChannelManager.onPlayerJoin(playerSetting, player);
    }

}
