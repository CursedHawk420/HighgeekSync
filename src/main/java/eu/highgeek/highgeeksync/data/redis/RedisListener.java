package eu.highgeek.highgeeksync.data.redis;

import eu.highgeek.highgeeksync.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RedisListener implements Listener {
    @EventHandler
    public void onRedisSetEvent(RedisInventorySetEvent event){
        Main.logger.warning("RedisSetEvent event fired!");

    }
}
