package eu.highgeek.highgeeksync.listeners;

import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.features.chat.ChannelManager;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private RedisManager redisManager;
    private ChannelManager channelManager;

    public PlayerJoinListener(RedisManager redisManager, ChannelManager channelManager){
        this.redisManager = redisManager;
        this.channelManager = channelManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        HighgeekSync.getInstance().logger.warning("Player joined!");
        new HighgeekPlayer(event.getPlayer(), redisManager, channelManager);
        /*Bukkit.getScheduler().runTaskAsynchronously(HighgeekSync.getInstance(), new Runnable() {
            @Override
            public void run() {
                new HighgeekPlayer(event.getPlayer(), redisManager, channelManager);
            }
        });*/
    }
}
