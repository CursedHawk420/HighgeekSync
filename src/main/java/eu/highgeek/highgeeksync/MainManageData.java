package eu.highgeek.highgeeksync;

import eu.highgeek.highgeeksync.backup.BackupHandler;
import eu.highgeek.highgeeksync.backup.CustomSyncSettings;
import eu.highgeek.highgeeksync.listener.DeathListener;
import eu.highgeek.highgeeksync.mysql.ManageMySQLData;
import eu.highgeek.highgeeksync.mysql.MySQL;
import eu.highgeek.highgeeksync.util.ConfigManager;
import eu.highgeek.highgeeksync.util.PlayerInventoryManager;
import eu.highgeek.highgeeksync.websync.objects.CustomMap;
import eu.highgeek.highgeeksync.websync.objects.VirtualInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.*;

public class MainManageData {

    public static StorageType storageType;

    public static ArrayList<Player> loadedPlayerData = new ArrayList<Player>();
    //public static ArrayList<String> inventoriesIds = new ArrayList<String>();
    public static HashMap<String, String> inventoriesIds = new HashMap<String, String>();
    public static HashMap<Player, ArrayList<String>> commandHashMap = new HashMap<Player, ArrayList<String>>();
    public static HashMap<String ,ArrayList<UUID>> openedInventories = new HashMap<String , ArrayList<UUID>>();
    public static HashMap<UUID, ArrayList<String>> playerInventoriesHashMap = new HashMap<UUID, ArrayList<String>>();
    public static HashMap<String, VirtualInventory> inventoriesObjects = new HashMap<String, VirtualInventory>();
    public static HashMap<UUID, VirtualInventory> playersDefaultInventories = new HashMap<UUID, VirtualInventory>();

    public static void initialize() {
        try {
            storageType = StorageType.valueOf(ConfigManager.getString("settings.storageType"));
        } catch (Exception ignored) {
            Main.logger.severe("No valid StorageType is set in Config!\n Disabling Plugin!");
            Bukkit.getPluginManager().disablePlugin(Main.main);
        }
        if (storageType == StorageType.MYSQL) {
            MySQL.connectMySQL();
            try {
                MySQL.registerMySQL();
            } catch (SQLException ignored) {
                Main.logger.severe("Could not initialize Database!\n Disabling Plugin!");
                Bukkit.getPluginManager().disablePlugin(Main.main);
            }
        }
        BackupHandler.initialize();
    }

    public static void reload() {
        BackupHandler.shutdown();

        try {
            storageType = StorageType.valueOf(ConfigManager.getString("settings.storageType"));
        } catch (Exception exception) {
            Main.logger.severe("No valid StorageType is set in Config!\n Disabling Plugin!");
            Bukkit.getPluginManager().disablePlugin(Main.main);
        }

        if (storageType == StorageType.MYSQL) {
            if (MySQL.isConnected()) {
                MySQL.disconnectMySQL();
            }
            MySQL.connectMySQL();
            try {
                MySQL.registerMySQL();
            } catch (SQLException ignored) {
                Main.logger.severe("Could not initialize Database!\n Disabling Plugin!");
                Bukkit.getPluginManager().disablePlugin(Main.main);
            }
        }

        BackupHandler.initialize();
    }

    public static void startShutdown() {
        BackupHandler.shutdown();

        Collection<Player> players = (Collection<Player>) Bukkit.getOnlinePlayers();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.iterator().next();
            savePlayer(player);
            if (i == players.size() - 1) {
                shutdown();
            }
        }
    }

    public static void shutdown() {
        if (storageType == StorageType.MYSQL) {
            MySQL.disconnectMySQL();
        }
    }

    public static Boolean isPlayerKnown(Player player) {
        if (storageType == StorageType.MYSQL) {
            return ManageMySQLData.isPlayerInDB(player);
        }
        return false;
    }

    public static void generatePlayer(Player player) {
        if (storageType == StorageType.MYSQL) {
            ManageMySQLData.generatePlayer(player);
        }
    }

    public static void loadPlayer(Player player) {
        if (storageType == StorageType.MYSQL) {
            ManageMySQLData.loadPlayer(player);
        }
    }

    public static void savePlayer(Player player) {
        if (DeathListener.deadPlayers.contains(player)) {
            player.getInventory().clear();
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setLevel(0);
        }
        try {
            player.getInventory().addItem(player.getItemOnCursor());
            player.setItemOnCursor(new ItemStack(Material.AIR));
        } catch (Exception ignored) { }
        if (storageType == StorageType.MYSQL) {
            ManageMySQLData.savePlayer(player, PlayerInventoryManager.saveItems(player, player.getInventory()), PlayerInventoryManager.saveEChest(player));
        }
    }

    public static void savePlayer(Player player, CustomSyncSettings customSyncSettings) {
        try {
            player.getInventory().addItem(player.getItemOnCursor());
            player.setItemOnCursor(new ItemStack(Material.AIR));
        } catch (Exception ignored) { }
        if (storageType == StorageType.MYSQL) {
            ManageMySQLData.savePlayer(player, customSyncSettings);
        }
    }


    public static ArrayList<String> getPlayerInventoriesFromMemory(Player player){
        return playerInventoriesHashMap.get(player.getUniqueId());
    }

    public enum StorageType {

        MYSQL,
        MONGODB,
        CLOUD; // For a future Update

    }
}
