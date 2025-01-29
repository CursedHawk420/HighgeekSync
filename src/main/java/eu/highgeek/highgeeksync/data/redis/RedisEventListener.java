package eu.highgeek.highgeeksync.data.redis;

import static eu.highgeek.highgeeksync.Main.*;

import eu.highgeek.highgeeksync.events.RedisEconomyPayEvent;
import eu.highgeek.highgeeksync.menus.ChannelSelector;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.common.Common;
import eu.highgeek.highgeeksync.events.AsyncRedisChatSetEvent;
import eu.highgeek.highgeeksync.events.RedisInventorySetEvent;
import eu.highgeek.highgeeksync.events.RedisNewInventoryEvent;
import eu.highgeek.highgeeksync.objects.Message;
import eu.highgeek.highgeeksync.objects.PlayerSettings;
import eu.highgeek.highgeeksync.sync.chat.ChannelManager;
import eu.highgeek.highgeeksync.sync.inventory.InventoryManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisEventListener extends JedisPubSub {

    public static Jedis listener;
    private static final Gson gson = new GsonBuilder().create();
    public Plugin plugin;

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        Main.logger.warning("onPSubscribe " + pattern + " " + subscribedChannels);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {

        if (channel.contains("set")){
            //Main.logger.warning("onPMessage pattern: " + pattern + " channel: " + channel + " message: " + message);
            //Main.logger.warning("Key: " + getKey(message));
            String key = getKey(message);

            switch (key){
                case "vinv":
                    fireVinvEvent(message);
                    //Main.logger.warning("Switch vinv hit: " + message);
                    return;
                case "chat":
                    fireChatMessage(message);
                    //Main.logger.warning("Switch chat hit: " + message);
                    return;
                case "winv":
                    //Main.logger.warning("Switch winv hit: " + message);
                    return;
                case "newinventory":
                    fireNewInventoryEvent(message);
                    //Main.logger.warning("Switch newinventory hit: " + message);
                    return;
                case "players":
                    firePlayersEvent(message);
                    //Main.logger.warning("Switch players hit: " + message);
                case "economy":
                    fireEconomyEvent(message);
                    //Main.logger.warning("Switch economy hit: " + message);
                return;
                default:
                    return;
            }
        }
    }

    public static void fireEconomyEvent(String message){
        if (message.contains("pay")){
            //Main.logger.warning("fireEconomyPayEvent " + message);
            RedisEconomyPayEvent event = new RedisEconomyPayEvent(RedisManager.getStringRedis(message), message, true);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    public static void firePlayersEvent(String message){
        if (message.contains("settings")){
            PlayerSettings playerSettings = gson.fromJson(RedisManager.getStringRedis(message), PlayerSettings.class);
            if(Common.playerSettings.containsKey(playerSettings.playerName)){
                Common.playerSettings.replace(playerSettings.playerName, playerSettings);
                ChannelManager.joinPlayerToChannels(ChannelManager.getChatPlayer(playerSettings.playerName), playerSettings);
                if(ChannelManager.openedSelectors.containsKey(playerSettings.playerName)){
                    ChannelSelector channelSelector = ChannelManager.openedSelectors.get(playerSettings.playerName);
                    channelSelector.playerSettings = playerSettings;
                    channelSelector.initializeItems();
                }
            }
        }
    }

    public static void fireChatMessage(String message){
        AsyncRedisChatSetEvent asyncRedisChatSetEvent = new AsyncRedisChatSetEvent(gson.fromJson(RedisManager.getStringRedis(message), Message.class), message, true);
        Bukkit.getPluginManager().callEvent(asyncRedisChatSetEvent);
    }

    public static void fireNewInventoryEvent(String message){
        try {
            String uuid = message.substring(message.length() - 36, message.length());
            main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
                @Override
                public void run() {
                    RedisNewInventoryEvent redisNewInventoryEvent = new RedisNewInventoryEvent(uuid, message);
                    //Main.logger.warning("fireNewInventoryEvent uuid: " + uuid + " rawUuid: " + message);
                    Bukkit.getPluginManager().callEvent(redisNewInventoryEvent);
                }
            },0);
        }catch (Exception exception){
            Main.logger.warning("error: " + ExceptionUtils.getStackTrace(exception));
        }
    }

    public static void fireVinvEvent(String message){
        try {
            String uuid = message.substring(message.lastIndexOf(":") - 36, message.lastIndexOf(":"));
            int slotid = Integer.valueOf(message.substring(message.lastIndexOf(":")+1));

            if (InventoryManager.openedInventories.containsKey(uuid)){
                main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
                    @Override
                    public void run() {
                        RedisInventorySetEvent redisInventorySetEvent = new RedisInventorySetEvent(message, uuid, slotid);
                        Bukkit.getPluginManager().callEvent(redisInventorySetEvent);
                    }
                },0);
            }
        }catch (Exception exception){
            Main.logger.warning("error: " + ExceptionUtils.getStackTrace(exception));
        }
    }

    public static String getKey(String channel) {
        int index = channel.indexOf(':');

        if (index >= 0 && index < channel.length() - 1) {
            return channel.substring(0,index);
        }
        return channel;
    }


    public static void listenerStopper(){
        listener.disconnect();
        listener.close();
        Main.redisListenerTask.cancel();
    }
}
