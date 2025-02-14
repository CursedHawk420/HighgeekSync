package eu.highgeek.highgeeksync.features.virtualinventories;

import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.sql.entities.VirtualInventories;
import eu.highgeek.highgeeksync.features.adapters.ItemStackAdapter;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InventoriesManager {

    @Getter
    private HashMap<String, List<Player>> openedInventories = new HashMap<>();



    public InventoriesManager(){

    }

    public void openSpecificVirtualInventory(VirtualInventories vinv, Player player){
        String invPrefix = "vinv:"+vinv.getPlayerName()+":"+vinv.getInventoryUuid();
        Inventory inv = Bukkit.createInventory(new VirtualInventoryHolder(vinv.getInventoryUuid(), vinv, player, invPrefix), vinv.getSize(), "§3Virtual Chest " + vinv.getInventoryName());
        HighgeekSync.getInstance().logger.warning("Opening inventory for player: " + player.getName());


        for (int i = 0; i < vinv.getSize() ; i++) {
            inv.setItem(i, ItemStackAdapter.fromString(HighgeekSync.getRedisManager().getStringRedis(invPrefix + ":" + i)));
        }
        List<Player> players = HighgeekSync.getInventoriesManager().getOpenedInventories().get(vinv.getInventoryUuid());
        if(players == null){
            players = new ArrayList<>();
        }
        players.add(player);
        HighgeekSync.getInventoriesManager().getOpenedInventories().put(vinv.getInventoryUuid(), players);
        player.openInventory(inv);
    }
}
