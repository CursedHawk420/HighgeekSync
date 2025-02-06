package eu.highgeek.highgeeksync.data.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import eu.highgeek.highgeeksync.models.PlayerSettings;
import lombok.Getter;
import org.bukkit.entity.Player;
import redis.clients.jedis.*;
import eu.highgeek.highgeeksync.models.ChatMessage;
import eu.highgeek.highgeeksync.features.chat.ChannelManager;
import eu.highgeek.highgeeksync.models.ChatChannel;
import redis.clients.jedis.json.Path2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisManager {

    private volatile boolean running = true;
    private final static String host = HighgeekSync.getInstance().config.getRedisIp();
    private final static int port = HighgeekSync.getInstance().config.getRedisPort();

    private final static HostAndPort node = HostAndPort.from(host + ":" + port);

    private final static JedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
            .resp3() // RESP3 protocol
            .build();

    public final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final JedisPooled jedisPooled;
    @Getter
    private final UnifiedJedis unifiedJedis;

    private final RedisSubscriber subscriber;

    public RedisManager(){
        this.unifiedJedis  = new UnifiedJedis(node, clientConfig);
        this.jedisPooled  = new JedisPooled(host,port);

        this.subscriber = new RedisSubscriber(this);

        HighgeekSync.getInstance().getServer().getScheduler().runTaskAsynchronously(HighgeekSync.getInstance(), () -> {
            startSubscriber();
        });
    }

    public void addChatEntry(ChatMessage message){
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

    public List<String> getGeneratedPlayerList(){
        ArrayList<String> list = new ArrayList<>();
        for (String string : getKeysPrefix("players:settings:*")){
            list.add(string.substring(string.lastIndexOf(":") + 1));
        }
        return list;
    }

    public String getStringRedis(String key){
        return this.jedisPooled.get(key);
    }

    public void setStringRedis(String key, String toSet){
        this.jedisPooled.set(key, toSet);
    }

    public Set<String> getKeysPrefix(String prefix){
        return jedisPooled.keys(prefix);
    }

    public void jsonSet(String uuid, String path, String toSet){
        unifiedJedis.jsonSet(uuid, new Path2(path), toSet);
    }

    public void jsonSet(String uuid, String toSet){
        unifiedJedis.jsonSet(uuid, toSet);
    }
}
