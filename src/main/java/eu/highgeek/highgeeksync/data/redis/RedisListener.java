package eu.highgeek.highgeeksync.data.redis;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import eu.highgeek.highgeeksync.Main;

public class RedisListener implements Listener {
    @EventHandler
    public void onRedisSetEvent(RedisInventorySetEvent event){
        Main.logger.warning("RedisSetEvent event fired!");

    }
}
