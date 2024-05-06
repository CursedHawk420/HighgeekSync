package eu.highgeek.highgeeksync.listener;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.MainManageData;
import eu.highgeek.highgeeksync.api.SyncSettings;
import eu.highgeek.highgeeksync.api.events.GeneratingPlayerProfileEvent;
import eu.highgeek.highgeeksync.api.events.ProcessingLoadingPlayerDataEvent;
import eu.highgeek.highgeeksync.mysql.MySQL;
import eu.highgeek.highgeeksync.util.ConfigManager;
import eu.highgeek.highgeeksync.websync.DataManager;
import eu.highgeek.highgeeksync.websync.data.ManageMysqlData;
import eu.highgeek.highgeeksync.websync.objects.VirtualInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.ArrayList;
import java.util.Date;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!MySQL.isConnected()) {
            MySQL.connectMySQL();
        }
        if (ConfigManager.getBoolean("settings.onlySyncPermission") && !player.hasPermission("sync.sync")) return;
        MainManageData.loadedPlayerData.add(player);
        MainManageData.commandHashMap.put(player, new ArrayList<String>());

        MainManageData.playerInventoriesHashMap.put(player.getUniqueId(), new ArrayList<String>());
        DataManager.loadPlayerInventoriesUuids(player);
        ManageMysqlData.getDefaultInventory(player);


        if (DeathListener.deadPlayers.contains(player)) {
            DeathListener.deadPlayers.remove(player);
        }
        if (!MainManageData.isPlayerKnown(player)) {
            if (ConfigManager.getBoolean("settings.sending.generated")) {
                player.sendMessage(ConfigManager.getColoredString("messages.generated"));
            }
            MainManageData.generatePlayer(player);
            Bukkit.getPluginManager().callEvent(new GeneratingPlayerProfileEvent(player));
            if (!ConfigManager.getBoolean("settings.usingOldData")) {
                if (ConfigManager.getBoolean("settings.syncing.enderchest")) {
                    player.getEnderChest().clear();
                }
                if (ConfigManager.getBoolean("settings.syncing.inventory")) {
                    player.getInventory().clear();
                }
                if (ConfigManager.getBoolean("settings.syncing.exp")) {
                    player.setLevel(0);
                }
            }
        } else {
            if (ConfigManager.getBoolean("settings.syncing.enderchest")) {
                player.getEnderChest().clear();
            }
            if (ConfigManager.getBoolean("settings.syncing.inventory")) {
                player.getInventory().clear();
            }
            if (ConfigManager.getBoolean("settings.syncing.exp")) {
                player.setLevel(0);
            }
        }

        player.sendMessage(ConfigManager.getColoredString("messages.loading"));
        Bukkit.getPluginManager().callEvent(new ProcessingLoadingPlayerDataEvent(player, new SyncSettings()));
        Bukkit.getScheduler().runTaskLater(Main.main, new Runnable() {
            @Override
            public void run() {
                MainManageData.loadPlayer(player);
            }
        }, 40l);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (MainManageData.loadedPlayerData.contains(event.getPlayer())) event.setCancelled(true);
        if (DeathListener.deadPlayers.contains(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!MainManageData.loadedPlayerData.contains(event.getPlayer())) return;
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() != Material.AIR) event.setCancelled(true);
    }
}
