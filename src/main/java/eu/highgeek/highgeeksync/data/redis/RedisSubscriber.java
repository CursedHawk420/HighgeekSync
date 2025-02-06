package eu.highgeek.highgeeksync.data.redis;


import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.redis.events.AsyncRedisChatEvent;
import eu.highgeek.highgeeksync.data.redis.events.AsyncRedisEconomyPayEvent;
import eu.highgeek.highgeeksync.models.ChatMessage;
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
                    /*
                case "vinv":
                    fireVinvEvent(message);
                    //Main.logger.warning("Switch vinv hit: " + message);
                    return;
                case "winv":
                    //Main.logger.warning("Switch winv hit: " + message);
                    return;
                case "players":
                    firePlayersEvent(message);
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


    public void fireEconomyEvent(String message){
        if (message.contains("pay")){
            //Main.logger.warning("fireEconomyPayEvent " + message);
            AsyncRedisEconomyPayEvent event = new AsyncRedisEconomyPayEvent(redisManager.getStringRedis(message), message, true);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    public void fireChatMessage(String uuid){
        AsyncRedisChatEvent asyncRedisChatEvent = new AsyncRedisChatEvent(redisManager.gson.fromJson(redisManager.getStringRedis(uuid), ChatMessage.class), true);
        Bukkit.getPluginManager().callEvent(asyncRedisChatEvent);
        HighgeekSync.getInstance().logger.warning("RedisChat event fired'");
    }

    public static String getKey(String channel) {
        int index = channel.indexOf(':');

        if (index >= 0 && index < channel.length() - 1) {
            return channel.substring(0,index);
        }
        return channel;
    }
}
