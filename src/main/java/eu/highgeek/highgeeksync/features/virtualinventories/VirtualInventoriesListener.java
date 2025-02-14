package eu.highgeek.highgeeksync.features.virtualinventories;

import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.data.redis.events.AsyncRedisInventorySetEvent;
import eu.highgeek.highgeeksync.data.sql.entities.VirtualInventories;
import eu.highgeek.highgeeksync.features.adapters.ItemStackAdapter;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class VirtualInventoriesListener implements Listener {

    private final RedisManager redisManager;
    private final InventoriesManager inventoriesManager;
    public VirtualInventoriesListener(RedisManager redisManager, InventoriesManager inventoriesManager){
        this.redisManager = redisManager;
        this.inventoriesManager = inventoriesManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        if(player.getOpenInventory().getTopInventory().getHolder() instanceof VirtualInventoryHolder vinv){
            if(!redisManager.isWorking()){
                event.setCancelled(true);
                return;
            }
            if (event.getCurrentItem().getType() == Material.BARRIER){
                event.setCancelled(true);
                return;
            }else {
                InventoryAction action = event.getAction();
                if(event.getClickedInventory().getHolder() instanceof VirtualInventoryHolder){
                    if (action != InventoryAction.NOTHING && action == InventoryAction.MOVE_TO_OTHER_INVENTORY){
                        redisManager.setStringRedis(vinv.getInvPrefix() + ":" + event.getSlot(),ItemStackAdapter.toString(new ItemStack(Material.AIR)));
                    }
                    else {
                        event.setCancelled(true);
                    }
                }
                else
                {
                    final ItemStack item = event.getCurrentItem();
                    final ItemStack[] oldvinv = player.getOpenInventory().getTopInventory().getContents();
                    if (action != InventoryAction.NOTHING && action == InventoryAction.MOVE_TO_OTHER_INVENTORY){
                        HighgeekSync.getInstance().server.getScheduler().scheduleSyncDelayedTask(HighgeekSync.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                ItemStack[] newvinv = player.getOpenInventory().getTopInventory().getContents();
                                for (int i = 0; i < newvinv.length; i++){
                                    if (oldvinv[i] != newvinv[i]){
                                        if (newvinv[i].getType() != Material.BARRIER){
                                            redisManager.setStringRedis(vinv.getInvPrefix() + ":" + i,ItemStackAdapter.toString(newvinv[i]));
                                        }
                                    }
                                }
                            }
                        },2);
                    }else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRedisInventorySetEvent(AsyncRedisInventorySetEvent event){
        List<Player> players = inventoriesManager.getOpenedInventories().get(event.getInvUuid());
        for (var item : players
        ) {
            ItemStack newItem = ItemStackAdapter.fromString(redisManager.getStringRedis(event.getRawMessage()));
            item.getOpenInventory().getTopInventory().setItem(event.getSlotId(), newItem);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if(event.getInventory().getHolder() instanceof VirtualInventoryHolder vinv){
            List<Player> players = inventoriesManager.getOpenedInventories().get(vinv.getUuid());
            players.remove(vinv.getPlayer());
            if(players.isEmpty()){
                inventoriesManager.getOpenedInventories().remove(vinv.getUuid());
            }else {
                inventoriesManager.getOpenedInventories().put(vinv.getUuid(), players);
            }
        }
    }
    public static <T> int indexOf(T[] arr, T val) {
        return Arrays.asList(arr).indexOf(val);
    }
}
