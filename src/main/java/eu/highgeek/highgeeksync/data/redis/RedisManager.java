package eu.highgeek.highgeeksync.data.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.highgeek.highgeeksync.HighgeekSync;
import redis.clients.jedis.*;
import eu.highgeek.highgeeksync.objects.Message;

public class RedisManager {

    private volatile boolean running = true;
    private final static String host = HighgeekSync.getInstance().config.getRedisIp();
    private final static int port = HighgeekSync.getInstance().config.getRedisPort();

    private final static HostAndPort node = HostAndPort.from(host + ":" + port);

    private final static JedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
            .resp3() // RESP3 protocol
            .build();

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final JedisPooled jedisPooled;
    private final UnifiedJedis unifiedJedis;

    private final RedisSubscriber subscriber;

    public RedisManager(){
        this.unifiedJedis  = new UnifiedJedis(node, clientConfig);
        this.jedisPooled  = new JedisPooled(host,port);

        this.subscriber = new RedisSubscriber();

        HighgeekSync.getInstance().getServer().getScheduler().runTaskAsynchronously(HighgeekSync.getInstance(), () -> {
            startSubscriber();
        });
    }

    public void addChatEntry(Message message){
        jedisPooled.set(message.getUuid(), gson.toJson(message));
    }

    private void startSubscriber(){
        while (running) {  // Infinite loop for reconnection handling
            try (Jedis jedis = new Jedis(host, port)) {
                HighgeekSync.getInstance().logger.info("Connected to Redis, waiting for messages...");
                jedis.psubscribe(subscriber, "*");
            } catch (Exception e) {
                if (!running) break; // Exit loop if shutting down
                HighgeekSync.getInstance().logger.info("Connection lost, retrying in 100 ms...");
                try {
                    Thread.sleep(100); // Wait before reconnecting
                } catch (InterruptedException ignored) {
                }
            }
        }
    }
    public void stopSubscriber() {
        running = false; // Set flag to stop reconnection attempts
        if (subscriber != null) {
            subscriber.punsubscribe(); // Stop Jedis subscription
        }
        HighgeekSync.getInstance().logger.info("Subscriber has been shut down.");
    }
}
