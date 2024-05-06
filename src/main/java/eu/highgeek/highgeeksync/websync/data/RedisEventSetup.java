package eu.highgeek.highgeeksync.websync.data;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.MainManageData;
import eu.highgeek.highgeeksync.websync.events.RedisSetEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import static eu.highgeek.highgeeksync.Main.main;


//https://bukkit.org/threads/custom-events.5190/
//https://www.spigotmc.org/wiki/using-the-event-api/
public class RedisEventSetup extends JedisPubSub {

    public static Jedis listener;
    public static BukkitTask listenertask;
    public Plugin plugin;


    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        Main.logger.warning("onPSubscribe " + pattern + " " + subscribedChannels);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        //Main.logger.warning("New EVENT MESSAGE: " + message);
        if (channel.contains("set")){
            try {
                String uuid = message.substring(message.lastIndexOf(":") - 36, message.lastIndexOf(":"));
                //Main.logger.warning("New EVENT UUID: " + uuid);
                int slotid = Integer.valueOf(message.substring(message.lastIndexOf(":")+1));
                if (MainManageData.openedInventories.containsKey(uuid)){
                    main.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
                        @Override
                        public void run() {
                            RedisSetEvent redisEvent = new RedisSetEvent(message, uuid, slotid);
                            Bukkit.getPluginManager().callEvent(redisEvent);
                            //Main.logger.warning("firing event with message: " + message + " uuid: " + uuid + " slotid: " + slotid);
                        }
                    },0);
                }
            }catch (Exception exception){

            }
        }
        //Main.logger.warning("onPMessage pattern: " + pattern + " channel: " + channel + " message: " + message);
    }
    public static void listenerStarter(Plugin plugin, Jedis jedis){
        listener = jedis;

        listenertask = Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            public void run() {
                listener.psubscribe(new RedisEventSetup(), "*");
            }
        });
    }

}