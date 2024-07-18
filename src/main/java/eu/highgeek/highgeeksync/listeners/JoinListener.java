package eu.highgeek.highgeeksync.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.highgeek.highgeeksync.sync.inventory.InventoryManager;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        InventoryManager.onPlayerJoin(event.getPlayer());
    }
}
