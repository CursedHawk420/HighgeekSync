package eu.highgeek.highgeeksync.listeners;

import dev.jorel.commandapi.annotations.Command;
import eu.highgeek.highgeeksync.common.Common;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.sync.inventory.InventoryManager;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Main.logger.warning("Player joined - firing onPlayerJoin()");

        Common.onPlayerJoin(event.getPlayer());
    }
}
