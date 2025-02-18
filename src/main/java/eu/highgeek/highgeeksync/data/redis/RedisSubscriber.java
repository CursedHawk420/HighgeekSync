package eu.highgeek.highgeeksync.data.redis;


import com.comphenix.protocol.PacketType;
import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.redis.events.AsyncRedisChatEvent;
import eu.highgeek.highgeeksync.data.redis.events.AsyncRedisEconomyPayEvent;
import eu.highgeek.highgeeksync.data.redis.events.AsyncRedisInventorySetEvent;
import eu.highgeek.highgeeksync.features.virtualinventories.InventoriesManager;
import eu.highgeek.highgeeksync.models.ChatMessage;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import eu.highgeek.highgeeksync.models.PlayerSettings;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import redis.clients.jedis.*;

public class RedisSubscriber extends JedisPubSub{

    private final RedisManager redisManager;
    public RedisSubscriber(RedisManager redisManager){
        this.redisManager = redisManager;
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        //HighgeekSync.getInstance().logger.info("Received message: " + message + " on channel: " + channel);
        if (channel.contains("set")){
            //Main.logger.warning("onPMessage pattern: " + pattern + " channel: " + channel + " message: " + message);
            //Main.logger.warning("Key: " + getKey(message));
            String key = getKey(message);

            switch (key){
                case "chat":
                    fireChatMessage(message);
                    //Main.logger.warning("Switch chat hit: " + message);
                    return;
                case "economy":
                    fireEconomyEvent(message);
                    //Main.logger.warning("Switch economy hit: " + message);
                    return;
                case "players":
                    firePlayersEvent(message);
                    return;
                case "vinv":
                    fireVinvEvent(message);
                    //Main.logger.warning("Switch vinv hit: " + message);
                    return;
                    /*
                case "winv":
                    //Main.logger.warning("Switch winv hit: " + message);
                    return;
                    //Main.logger.warning("Switch players hit: " + message);
                    */
                default:
                    return;
            }
        }
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        HighgeekSync.getInstance().logger.info("Subscribed to: " + pattern);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        HighgeekSync.getInstance().logger.info("Unsubscribed from: " + pattern);
    }



    public void fireVinvEvent(String message){
        try {
            String uuid = message.substring(message.lastIndexOf(":") - 36, message.lastIndexOf(":"));
            int slotid = Integer.valueOf(message.substring(message.lastIndexOf(":")+1));

            if (HighgeekSync.getInventoriesManager().getOpenedInventories().containsKey(uuid)){
                HighgeekSync.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(HighgeekSync.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        AsyncRedisInventorySetEvent redisInventorySetEvent = new AsyncRedisInventorySetEvent(message, uuid, slotid, false);
                        Bukkit.getPluginManager().callEvent(redisInventorySetEvent);
                    }
                },0);
            }
        }catch (Exception exception){
            HighgeekSync.getInstance().logger.warning("error: " + ExceptionUtils.getStackTrace(exception));
        }
    }

    public void fireEconomyEvent(String uuid){
        if (uuid.contains("pay")){
            AsyncRedisEconomyPayEvent event = new AsyncRedisEconomyPayEvent(redisManager.getStringRedis(uuid), uuid, true);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    public void fireChatMessage(String uuid){
        AsyncRedisChatEvent asyncRedisChatEvent = new AsyncRedisChatEvent(redisManager.gson.fromJson(redisManager.getStringRedis(uuid), ChatMessage.class), true);
        Bukkit.getPluginManager().callEvent(asyncRedisChatEvent);
    }

    public void firePlayersEvent(String uuid){
        if(uuid.contains("settings")){
            String playerName = uuid.substring(uuid.lastIndexOf(':') + 1, uuid.length());
            HighgeekPlayer player = HighgeekSync.getInstance().getHighgeekPlayers().get(playerName);
            if(player != null){
                PlayerSettings newPlayerSettings = redisManager.gson.fromJson(redisManager.getStringRedis(uuid), PlayerSettings.class);
                newPlayerSettings.player = player;
                player.setPlayerSettings(newPlayerSettings);
                player.initChannels();
                HighgeekSync.getChannelManager().getOpenedChannelMenus().get(player.getPlayer().getName()).init();
            }
        }
    }

    public static String getKey(String channel) {
        int index = channel.indexOf(':');

        if (index >= 0 && index < channel.length() - 1) {
            return channel.substring(0,index);
        }
        return channel;
    }
}
