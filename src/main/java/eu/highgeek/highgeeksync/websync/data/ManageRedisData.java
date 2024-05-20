package eu.highgeek.highgeeksync.websync.data;

import com.google.gson.*;
import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.util.ConfigManager;
import eu.highgeek.highgeeksync.websync.adapters.item.ItemStackAdapter;
import eu.highgeek.highgeeksync.websync.chat.Message;
import eu.highgeek.highgeeksync.websync.objects.VirtualInventory;
import mineverse.Aust1n46.chat.command.chat.Me;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.exceptions.JedisException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import static eu.highgeek.highgeeksync.MainManageData.inventoriesIds;
import static eu.highgeek.highgeeksync.MainManageData.inventoriesObjects;


public class ManageRedisData {
    public static String host = ConfigManager.getString("redis.host");
    public static int port = ConfigManager.config.getInt("redis.port");
    public static String database = ConfigManager.getString("redis.database");
    public static String username = ConfigManager.getString("redis.username");
    public static String password = ConfigManager.getString("redis.password");
    public static String inventoryPrefix = "vinv:";

    public static JedisPool pool = new JedisPool(host, port);
    public static JedisPooled pooled = new JedisPooled(host, port);
    //final static Gson gson = new Gson();
    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();


    public static Jedis setupRedis() {
        final GenericObjectPoolConfig<Jedis> poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(0);
        Jedis jedis;
        try (JedisPool pool = new JedisPool(poolConfig, "10.220.22.126", 6379)) {
            jedis = pool.getResource();
            jedis.connect();
            return jedis;
        }catch (JedisException e){

            Bukkit.getPluginManager().disablePlugin(Main.main);
            return null;
        }
    }
    public static void setInventoryItem(String uuid, int position, ItemStack item){
        try  {
            if (item.getType() == Material.BARRIER){
                return;
            }

            String playerName = inventoriesIds.get(uuid);

            if (playerName == null){
                playerName = inventoriesObjects.get(uuid).getPlayerName();
            }

            String itemUuid = inventoryPrefix + playerName + ":" + uuid + ":" + position;

            //String json = gson.toJson(item.serialize());


            String json = ItemStackAdapter.itemStackToString(item);
            //String json = JsonItemStack.toJson(item);

            Main.redisConnection.mset(itemUuid, json);



            Main.logger.info("connection.mset Redis data with key: "+itemUuid);

        }
        catch (JedisException exception){
            Main.logger.severe("Jedis Pool error\n"+exception);
            //Bukkit.getPluginManager().disablePlugin(Main.main);
        }
    }
    public static void setDefaultInventoryItem(String uuid, int position, VirtualInventory inv, ItemStack item){
        try  {
            if (item.getType() == Material.BARRIER){
                return;
            }
            String prefix = "vinv";
            if (inv.isIsWeb()){
                prefix = "winv";
            }
            String itemUuid = prefix+":" + inv.getPlayerName()+ ":" + uuid + ":" + position;

            //String json = gson.toJson(item.serialize());


            String json = ItemStackAdapter.itemStackToString(item);
            //String json = JsonItemStack.toJson(item);

            Main.redisConnection.mset(itemUuid, json);



            Main.logger.info("connection.mset Redis data with key: "+itemUuid);

        }
        catch (JedisException exception){
            Main.logger.severe("Jedis Pool error\n"+exception);
            //Bukkit.getPluginManager().disablePlugin(Main.main);
        }
    }
    public static ItemStack getInventoryItem(VirtualInventory vinv, int position){
        try  {
            String itemUuid = inventoryPrefix + vinv.getPlayerName() + ":" + vinv.getInvUuid() + ":" + position;
//            String json = null;
//            try {
//                json = Main.redisConnection.get(itemUuid);
//            }catch (NullPointerException e){
//                return new ItemStack(Material.AIR);
//            }

            String json = Main.redisConnection.get(itemUuid);


            /*Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
            ItemStack item = ItemStack.deserialize(map);*/

            ItemStack item = ItemStackAdapter.stringToItemStack(json);
            //ItemStack item = JsonItemStack.fromJson(json);

            Main.logger.info("connection.get Redis data with key: "+itemUuid);

            return item;
        }
        catch (JedisException exception){
            Main.logger.severe("Jedis Pool error\n"+exception);
            //Bukkit.getPluginManager().disablePlugin(Main.main);
            return null;
        }
    }
    public static ItemStack getInventoryItem(String uuid, int position){
        try  {
            VirtualInventory vinv = inventoriesObjects.get(uuid);
            String itemUuid = inventoryPrefix + vinv.getPlayerName() + ":" + uuid + ":" + position;
//            String json = null;
//            try {
//                json = Main.redisConnection.get(itemUuid);
//            }catch (NullPointerException e){
//                return new ItemStack(Material.AIR);
//            }

            String json = Main.redisConnection.get(itemUuid);


            /*Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>(){}.getType());
            ItemStack item = ItemStack.deserialize(map);*/

            ItemStack item = ItemStackAdapter.stringToItemStack(json);
            //ItemStack item = JsonItemStack.fromJson(json);

            Main.logger.info("connection.get Redis data with key: "+itemUuid);

            return item;
        }
        catch (JedisException exception){
            Main.logger.severe("Jedis Pool error\n"+exception);
            //Bukkit.getPluginManager().disablePlugin(Main.main);
            return null;
        }
    }
    public static String getInventoryItemString(String uuid, int position){
        try  {

            String itemUuid = inventoryPrefix + inventoriesIds.get(uuid)+ ":" + uuid + ":" + position;

            String json = Main.redisConnection.get(itemUuid).toString();

            Main.logger.info("connection.get Redis data with key: "+itemUuid);

            return json;
        }
        catch (JedisException exception){
            Main.logger.severe("Jedis Pool error\n"+exception);
            //Bukkit.getPluginManager().disablePlugin(Main.main);
            return null;
        }
    }
    public static void addChatEntry(Message message){
        Main.redisConnection.set(message.getUuid(), gson.toJson(message));
        Main.logger.warning("addChatEntry triggered ");
    }
    public static String getEntry(String uuid){
        return Main.redisConnection.get(uuid);
    }

//    public static void setInventoryItem(String uuid, int possition, ItemStack item){
//        try  {
//            String itemUuid = uuid+":"+possition;
//
//            String json = ItemAdapter.toJson(item);
//
//            pooled.jsonSet(itemUuid, json);
//
//            Main.logger.severe("jsonSet Redis data with key: "+itemUuid);
//
//        }
//        catch (JedisException exception){
//            Main.logger.severe("Jedis Pool error\n"+exception);
//            //Bukkit.getPluginManager().disablePlugin(Main.main);
//        }
//    }
//    public static ItemStack getInventoryItem(String uuid, int possition){
//        try  {
//            String itemUuid = uuid+":"+possition;
//
//            String json = pooled.jsonGet(itemUuid).toString();
//
//            ItemStack item = ItemAdapter.fromJson(json);
//
//            Main.logger.severe("jsonGet Redis data with key: "+itemUuid);
//
//            return item;
//        }
//        catch (JedisException exception){
//            Main.logger.severe("Jedis Pool error\n"+exception);
//            //Bukkit.getPluginManager().disablePlugin(Main.main);
//            return null;
//        }
//    }
/*
    public static String getString(String uuid){
        try (Jedis jedis = pool.getResource()) {
            String data = jedis.get(uuid);
            Main.logger.severe("get Redis data with key: "+uuid);
            return data;
        }
        catch (JedisException exception){
            Main.logger.severe("Jedis Pool error\n"+exception);
            //Bukkit.getPluginManager().disablePlugin(Main.main);
            return null;
        }
    }
    public static void setChest(String uuid, String data){
        try (Jedis jedis = pool.getResource()) {
            jedis.mset(uuid, data);
            Main.logger.severe("set Redis data with key: "+uuid);
        }
        catch (JedisException exception){
            Main.logger.severe("Jedis Pool error\n"+exception);
            //Bukkit.getPluginManager().disablePlugin(Main.main);
        }
    }
    public static Object getJson(String uuid){
        try  {
            Object json = pooled.jsonGet(uuid);
            Main.logger.severe("jsonGet Redis data with key: "+uuid);
            return json;
        }
        catch (JedisException exception){
            Main.logger.severe("Jedis Pool error\n"+exception);
            //Bukkit.getPluginManager().disablePlugin(Main.main);
            return null;
        }
    }
    public static void setJson(String uuid, String data){
        try  {
            pooled.jsonSet(uuid, gson.toJson(data));
            Main.logger.severe("jsonSet Redis data with key: "+uuid);
        }
        catch (JedisException exception){
            Main.logger.severe("Jedis Pool error\n"+exception);
            //Bukkit.getPluginManager().disablePlugin(Main.main);
        }
    }*/
}