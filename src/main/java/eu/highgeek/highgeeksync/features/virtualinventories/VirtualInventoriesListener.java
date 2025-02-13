package eu.highgeek.highgeeksync.features.virtualinventories;

import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.data.redis.events.AsyncRedisInventorySetEvent;
import eu.highgeek.highgeeksync.data.sql.entities.VirtualInventories;
import eu.highgeek.highgeeksync.features.adapters.ItemStackAdapter;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class VirtualInventoriesListener implements Listener {

    private RedisManager redisManager;
    private InventoriesManager inventoriesManager;
    public VirtualInventoriesListener(RedisManager redisManager, InventoriesManager inventoriesManager){
        this.redisManager = redisManager;
        this.inventoriesManager = inventoriesManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getInventory().getHolder() instanceof VirtualInventoryHolder vinv){
            //TODO: Full item manipulation logic
        }
    }

    @EventHandler
    public void onRedisInventorySetEvent(AsyncRedisInventorySetEvent event){
        List<HighgeekPlayer> players = inventoriesManager.getOpenedInventories().get(event.getInvUuid());
        for (var item : players
        ) {
            ItemStack newItem = ItemStackAdapter.fromString(redisManager.getStringRedis(event.getRawMessage()));
            item.getPlayer().getOpenInventory().getTopInventory().setItem(event.getSlotId(), newItem);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if(event.getInventory().getHolder() instanceof VirtualInventoryHolder vinv){
            List<HighgeekPlayer> players = inventoriesManager.getOpenedInventories().get(vinv.getUuid());
            players.remove(vinv.getPlayer());
            if(players.isEmpty()){
                inventoriesManager.getOpenedInventories().remove(vinv.getUuid());
            }else {
                inventoriesManager.getOpenedInventories().put(vinv.getUuid(), players);
            }
        }
    }
}
