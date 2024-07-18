package eu.highgeek.highgeeksync.data.redis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.objects.VirtualInventory;
import eu.highgeek.highgeeksync.sync.adapters.ItemStackAdapter;
import eu.highgeek.highgeeksync.utils.ConfigManager;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.bukkit.Bukkit;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisException;

public class RedisManager {

    public static String host = ConfigManager.getString("redis.host");
    public static String port = ConfigManager.config.getString("redis.port");
    public static String database = ConfigManager.getString("redis.database");
    public static String username = ConfigManager.getString("redis.username");
    public static String password = ConfigManager.getString("redis.password");

    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static boolean initRedis(){
        Main.redisConnection = RedisManager.setupRedis();
        if (Main.redisConnection != null){
            RedisEventListener.listenerStarter(Main.main, RedisManager.setupRedis());
            Main.logger.warning("Redis connected successfully! \n");
            return true;
        }else {
            Main.logger.warning("Redis connection failed! \n");
            return false;
        }


    }

    public static Jedis setupRedis() {
        Main.logger.warning("Redis connection string:  \n" + host + ":" + port);
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

    public static String getRedis(String key){
        return Main.redisConnection.get(key);
    }


    public static void setRedis(String key, String toSet){
        Main.redisConnection.set(key, toSet);
    }

    public static ItemStack getItemFromRedis(String uuid){
        return ItemStackAdapter.stringToItemStack(getRedis(uuid));
    }


    public static void setItemInRedis(String uuid, ItemStack item){
        setRedis(uuid, ItemStackAdapter.itemStackToString(item));
    }


    public static void generateInventoryInRedis(VirtualInventory virtualInventory, String invType){
        String prefix =  invType+":"+virtualInventory.PlayerName+":"+virtualInventory.InvUuid+":";
        for (int i = 0; i < virtualInventory.Size; i++) {
            setRedis(prefix+i, "{id:\"minecraft:air\"}");
        }
    }
}
