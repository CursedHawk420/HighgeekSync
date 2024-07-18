package eu.highgeek.highgeeksync.listeners;

import eu.highgeek.highgeeksync.data.sql.MysqlVirtualInventoryManager;
import eu.highgeek.highgeeksync.sync.inventory.InventoryManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        InventoryManager.onPlayerJoin(event.getPlayer());
    }
}
