package eu.highgeek.highgeeksync.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.common.Common;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Main.logger.warning("Player joined - firing onPlayerJoin()");

        Common.onPlayerJoin(event.getPlayer());
    }
}
