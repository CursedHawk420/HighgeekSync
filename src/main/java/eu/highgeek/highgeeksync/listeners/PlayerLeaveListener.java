package eu.highgeek.highgeeksync.listeners;

import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Bukkit.getScheduler().runTaskAsynchronously(HighgeekSync.getInstance(), new Runnable() {
            @Override
            public void run() {
                HighgeekPlayer player = HighgeekSync.getInstance().getHighgeekPlayers().get(event.getPlayer().getUniqueId());
                player.onDisconnectAsync();
                HighgeekSync.getInstance().getHighgeekPlayers().remove(event.getPlayer().getUniqueId());
            }
        });
    }
}
