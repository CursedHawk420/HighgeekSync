package eu.highgeek.highgeeksync.listeners;

import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.json.Path2;

import java.util.Objects;

public class StatsListener implements Listener {
    public static final String servername =  HighgeekSync.getInstance().config.getServerName();

    private RedisManager redisManager;
    public StatsListener(RedisManager redisManager){
        this.redisManager = redisManager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onStatsIncrement(PlayerStatisticIncrementEvent event){
        Bukkit.getScheduler().runTaskAsynchronously(HighgeekSync.getInstance(), () -> runAsync(event));
    }

    private void runAsync(PlayerStatisticIncrementEvent event){
        try {
            switch (event.getStatistic().getType()){
                case BLOCK, ITEM:{
                    try {
                        if(!Objects.equals(event.getMaterial(), null)){
                            redisManager.jsonSet("players:stats:" + event.getPlayer().getName()+":"+servername, event.getStatistic().name() + "." + event.getMaterial(), String.valueOf(event.getNewValue()));
                        }
                    }catch (JedisDataException exception){
                        redisManager.getUnifiedJedis().jsonSet("players:stats:" + event.getPlayer().getName()+":"+servername, new Path2(event.getStatistic().name()), "{\""+ event.getMaterial() +"\": \"" + String.valueOf(event.getNewValue()) +"\"}");
                    }
                }
                case ENTITY:{
                    try {
                        if(!Objects.equals(event.getEntityType(), null)){
                            redisManager.jsonSet("players:stats:" + event.getPlayer().getName()+":"+servername, event.getStatistic().name() + "." + event.getEntityType(), String.valueOf(event.getNewValue()));
                        }
                    }catch (JedisDataException exception){
                        redisManager.getUnifiedJedis().jsonSet("players:stats:" + event.getPlayer().getName()+":"+servername, new Path2(event.getStatistic().name()), "{\""+ event.getEntityType() +"\": \"" + String.valueOf(event.getNewValue()) +"\"}");
                    }
                }
                case UNTYPED:{
                    try {
                        redisManager.jsonSet("players:stats:" + event.getPlayer().getName()+":"+servername, "GENERAL." + event.getStatistic().name(), String.valueOf(event.getNewValue()));
                    }catch (JedisDataException exception){
                        redisManager.getUnifiedJedis().jsonSet("players:stats:" + event.getPlayer().getName()+":"+servername, new Path2("GENERAL"), "{\""+ event.getStatistic().name() +"\": \"" + String.valueOf(event.getNewValue()) +"\"}");
                    }
                }
            }
        }catch (JedisDataException exception){
            redisManager.jsonSet("players:stats:" + event.getPlayer().getName()+":"+servername, "{\n" +
                    "    \"DROP\": {},\n" +
                    "    \"PICKUP\": {},\n" +
                    "    \"MINE_BLOCK\": {},\n" +
                    "    \"USE_ITEM\": {},\n" +
                    "    \"BREAK_ITEM\": {},\n" +
                    "    \"CRAFT_ITEM\": {},\n" +
                    "    \"KILL_ENTITY\": {},\n" +
                    "    \"ENTITY_KILLED_BY\": {},\n" +
                    "    \"GENERAL\": {}\n" +
                    "}");
            HighgeekSync.getInstance().logger.warning("New stat counter created for player " + event.getPlayer().getName());
        }catch (Exception exception){
            HighgeekSync.getInstance().logger.warning("Error logging statistic. Exception" + exception.getMessage());
        }
    }
}
