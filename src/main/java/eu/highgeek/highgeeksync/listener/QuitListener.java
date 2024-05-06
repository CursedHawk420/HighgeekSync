package eu.highgeek.highgeeksync.listener;

import eu.highgeek.highgeeksync.MainManageData;
import eu.highgeek.highgeeksync.util.ConfigManager;
import eu.highgeek.highgeeksync.websync.DataManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (ConfigManager.getBoolean("settings.onlySyncPermission") && !player.hasPermission("sync.sync")) return;
        if (!MainManageData.loadedPlayerData.contains(player)) {
            MainManageData.savePlayer(player);
        } else {
            MainManageData.loadedPlayerData.remove(player);
        }

        if (MainManageData.playerInventoriesHashMap.containsKey(player.getUniqueId())){
            DataManager.savePlayerInventoriesToDatabase(player);
            MainManageData.playerInventoriesHashMap.remove(player);
        }
    }
}
