package eu.highgeek.highgeeksync.data.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import eu.highgeek.highgeeksync.objects.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.sync.adapters.ItemStackAdapter;
import eu.highgeek.highgeeksync.sync.chat.ChannelManager;
import eu.highgeek.highgeeksync.utils.ConfigManager;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.json.Path2;

public class RedisManager {

    private static boolean running = true;
    private static RedisEventListener subscriber = new RedisEventListener();
    public static String host = ConfigManager.getString("redis.host");
    public static String port = ConfigManager.config.getString("redis.port");
    public static String database = ConfigManager.getString("redis.database");
    public static String username = ConfigManager.getString("redis.username");
    public static String password = ConfigManager.getString("redis.password");
    public static HostAndPort node = HostAndPort.from(host + ":" + port);
    public static JedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
            .resp3() // RESP3 protocol
            .build();

    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static void initRedis(){
        try {
            Main.redisConnection = RedisManager.setupRedis();
            Main.redisListenerTask = Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.main, new Runnable() {
                public void run() {
                    startSubscriber();
                }
            });
        }catch (JedisException e){
            Main.logger.warning("Redis connection failed! \n" + ExceptionUtils.getStackTrace(e));
            Main.main.getServer().shutdown();
        }
    }

    private static void startSubscriber(){
        while (running) {  // Infinite loop for reconnection handling
            try (Jedis jedis = new Jedis(host, Integer.parseInt(port))) {
                jedis.psubscribe(subscriber, "*");
            } catch (Exception e) {
                if (!running) break; // Exit loop if shutting down
                try {
                    Thread.sleep(100); // Wait before reconnecting
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public static void stopSubscriber() {
        running = false; // Set flag to stop reconnection attempts
        if (subscriber != null) {
            subscriber.punsubscribe(); // Stop Jedis subscription
        }
        Main.redisListenerTask.cancel();
    }

    public static void initUnifiedJedis(){
        Main.unifiedJedis = new UnifiedJedis(node, clientConfig);
    }

    public static Jedis setupRedis() {
        final GenericObjectPoolConfig<Jedis> poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(0);
        Jedis jedis;
        try (JedisPool pool = new JedisPool(poolConfig, host, Integer.valueOf(port))) {
            jedis = pool.getResource();
            jedis.connect();
            return jedis;
        }catch (JedisException e){
            Main.logger.warning("Redis connection failed! \n" + e.getMessage());
            //Bukkit.getPluginManager().disablePlugin(Main.main);
            return null;
        }
    }

    public static String getStringRedis(String key){
        if(Main.redisConnection.getConnection() == null){
            initRedis();
        }
        return Main.redisConnection.get(key);
    }

    public static void setRedis(String key, String toSet){
        if(Main.redisConnection.getConnection() == null){
            initRedis();
        }
        Main.redisConnection.set(key, toSet);
    }

    public static ItemStack getItemFromRedis(String uuid){
        return ItemStackAdapter.stringToItemStack(getStringRedis(uuid));
    }


    public static void setItemInRedis(String uuid, ItemStack item){
        setRedis(uuid, ItemStackAdapter.itemStackToString(item));
    }

    public static Set<String> getKeysPrefix(String prefix){
        if(Main.redisConnection.getConnection() == null){
            initRedis();
        }
        return Main.redisConnection.keys(prefix);
    }


    public static void generateInventoryInRedis(VirtualInventory virtualInventory, String invType){
        String prefix =  invType+":"+virtualInventory.PlayerName+":"+virtualInventory.InvUuid+":";
        for (int i = 0; i < virtualInventory.Size; i++) {
            setRedis(prefix+i, "{\"id\":\"minecraft:air\"}");
        }
    }

    public static void setPlayerSettings(PlayerSettings playerSettings){
        setRedis("players:settings:"+playerSettings.playerName, gson.toJson(playerSettings));
    }

    public static PlayerSettings getPlayerSettings(Player player){
        String playerSettings = getStringRedis("players:settings:"+player.getName());
        if (playerSettings == null){
            PlayerSettings newPlayerSettings = new PlayerSettings(player.getName(), player.getUniqueId().toString(), ChannelManager.getDefaultChatChannels().stream().map(ChatChannel::getName).collect(Collectors.toList()), "global", false, new ArrayList<>());
            setPlayerSettings(newPlayerSettings);
            return newPlayerSettings;
        }else{
            return gson.fromJson(getStringRedis("players:settings:"+player.getName()), PlayerSettings.class);
        }
    }

    public static void addChatEntry(Message message){
        Main.redisConnection.set(message.getUuid(), gson.toJson(message));
    }

    public static List<String> getGeneratedPlayerList(){
        ArrayList<String> list = new ArrayList<>();
        for (String string : getKeysPrefix("players:settings:*")){
            list.add(string.substring(string.lastIndexOf(":") + 1));
        }
        return list;
    }

    public static void jsonSet(String uuid, String path, String toSet){
        Main.unifiedJedis.jsonSet(uuid, new Path2(path), toSet);
    }
    public static void jsonSet(String uuid, String toSet){
        Main.unifiedJedis.jsonSet(uuid, toSet);
    }
}
