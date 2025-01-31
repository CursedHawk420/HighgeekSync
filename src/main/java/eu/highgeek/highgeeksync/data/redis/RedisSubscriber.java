package eu.highgeek.highgeeksync.data.redis;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.highgeek.highgeeksync.HighgeekSync;
import redis.clients.jedis.*;

import java.util.concurrent.TimeUnit;

public class RedisSubscriber extends JedisPubSub{

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        HighgeekSync.getInstance().logger.info("Received message: " + message + " on channel: " + channel);
    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        HighgeekSync.getInstance().logger.info("Subscribed to: " + pattern);
    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        HighgeekSync.getInstance().logger.info("Unsubscribed from: " + pattern);
    }
}
