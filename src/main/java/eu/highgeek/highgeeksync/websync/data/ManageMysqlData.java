package eu.highgeek.highgeeksync.websync.data;


import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.MainManageData;
import eu.highgeek.highgeeksync.mysql.MySQL;
import eu.highgeek.highgeeksync.util.*;
import eu.highgeek.highgeeksync.websync.objects.VirtualInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import eu.highgeek.highgeeksync.websync.inventory.VirtualInventoryManager;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ManageMysqlData {


    public static String virtualinventorytablename = ConfigManager.getString("mysql.virtualinventorytablename");
    public static Boolean isInventoryInDB(String uuid) {
        if (!MySQL.isConnected()) {
            MySQL.connectMySQL();
        }
        try {
            PreparedStatement tryPreparedStatement = MySQL.getConnection().prepareStatement("SELECT p.last_updated FROM "+ virtualinventorytablename +" as p WHERE p.inventory_uuid = ?");
            tryPreparedStatement.setString(1, uuid);
            ResultSet rs = tryPreparedStatement.executeQuery();
            return rs.next();
        } catch (SQLException ignored) {
            return false;
        }
    }

    public static void generateInventory(String playeruuid, String playername, String uuid, String jsondata, String name, int size, boolean web) {
        if (!MySQL.isConnected()) {
            MySQL.connectMySQL();
        }
        if (isInventoryInDB(uuid)) return;
        try {
            PreparedStatement preparedStatement = MySQL.getConnection().prepareStatement("INSERT INTO "+ virtualinventorytablename +" (player_uuid, player_name, inventory_uuid, inventory_name, jsondata, last_updated, size, web) VALUES(?,?,?,?,?,?,?,?)");
            preparedStatement.setString(1, playeruuid);
            preparedStatement.setString(2, playername);
            preparedStatement.setString(3, uuid);
            preparedStatement.setString(4, name);
            preparedStatement.setString(5, jsondata);
            Date dateNow = new Date( );
            SimpleDateFormat simpleDateFormat =
                    new SimpleDateFormat ("MM.dd.yyyy G 'at' HH:mm:ss z");
            preparedStatement.setString(6, simpleDateFormat.format(dateNow));
            preparedStatement.setInt(7, size);
            preparedStatement.setBoolean(8, web);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            if (!MySQL.isConnected()) {
                MySQL.connectMySQL();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable() {
                    @Override
                    public void run() {
                        generateInventory(playeruuid, playername, uuid, jsondata, name, size, web);
                    }
                }, 20);
            } else {
                exception.printStackTrace();
                Main.logger.warning("Something went wrong with registering an Inventory!");
            }
        }
    }
    public static ArrayList<String> loadPlayerVirtualInventoriesUuids(Player player) {
        if (!MySQL.isConnected()) {
            MySQL.connectMySQL();
        }
        try {
            PreparedStatement preparedStatement = MySQL.getConnection().prepareStatement("SELECT * FROM "+ virtualinventorytablename +" as p WHERE p.player_uuid = ?");
            preparedStatement.setString(1, String.valueOf(player.getUniqueId()));
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<String> array = new ArrayList<String>();
            for (int i = 0; i <= resultSet.getFetchSize(); i++) {
                while (resultSet.next()){
                    array.add(resultSet.getString("inventory_uuid"));
                }
            }
            return array;

            //what to do with this

        } catch (SQLException exception) {
            if (!MySQL.isConnected()) {
                MySQL.connectMySQL();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable() {
                    @Override
                    public void run() {
                        loadPlayerVirtualInventoriesUuids(player);
                    }
                }, 20);
            } else {
                exception.printStackTrace();
                Main.logger.warning("Something went wrong with loading an Inventory!");
            }
            return null;
        }
    }

    public static void generateDefaultWebInventory(Player player){

        String uuid = UUID.randomUUID().toString();
        VirtualInventory vinv = new VirtualInventory();
        vinv.setIsWeb(true);
        vinv.setInvName("default");
        vinv.setSize(27);
        vinv.setOwnerUuid(player.getUniqueId().toString());
        vinv.setPlayerName(player.getName());
        vinv.setInvUuid(uuid);
        for (int i = 0 ; i < 27; i++){
            ManageRedisData.setDefaultInventoryItem(uuid,i, vinv, new ItemStack(Material.AIR));
        }
        saveVirtualInventoryInDatabase(player.getUniqueId().toString(), player.getName(), uuid, "empty", "default", 27, true);
    }
    public static void getDefaultInventory(Player player){
        if (!MySQL.isConnected()) {
            MySQL.connectMySQL();
        }
        if (MainManageData.playersDefaultInventories.containsKey(player.getUniqueId())){
            Main.logger.info("playersDefaultInventories already contains player");
            return;
        }
        try {
            PreparedStatement preparedStatement = MySQL.getConnection().prepareStatement("SELECT * FROM "+ virtualinventorytablename +" as p WHERE p.player_uuid = ? AND p.inventory_name = ?");
            preparedStatement.setString(1, String.valueOf(player.getUniqueId()));
            preparedStatement.setString(2, String.valueOf("default"));
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()){
                String uuid = UUID.randomUUID().toString();
                VirtualInventory vinv = new VirtualInventory();
                vinv.setIsWeb(false);
                vinv.setInvName("default");
                vinv.setSize(27);
                vinv.setOwnerUuid(player.getUniqueId().toString());
                vinv.setPlayerName(player.getName());
                vinv.setInvUuid(uuid);
                MainManageData.playersDefaultInventories.put(player.getUniqueId(), vinv);
                MainManageData.inventoriesObjects.put(vinv.getInvUuid(), vinv);
                for (int i = 0 ; i < 27; i++){
                    ManageRedisData.setDefaultInventoryItem(uuid,i,vinv, new ItemStack(Material.AIR));
                }
                saveVirtualInventoryInDatabase(player.getUniqueId().toString(), player.getName(), uuid, "empty", "default", 27, false);
                generateDefaultWebInventory(player);
            }else {
            while (resultSet.next()){
                if (!resultSet.getBoolean("web")){
                    VirtualInventory vinv = new VirtualInventory();
                    vinv.setInvUuid(resultSet.getString("inventory_uuid"));
                    vinv.setPlayerName(player.getName());
                    vinv.setInvName(resultSet.getString("inventory_name"));
                    vinv.setSize(resultSet.getInt("size"));
                    vinv.setOwnerUuid(player.getUniqueId().toString());
                    vinv.setIsWeb(false);
                    if (vinv.getInvName() == "default"){
                        Main.logger.info("getDefaultInventory - existing inventory loaded default to playersDefaultInventories");
                        MainManageData.playersDefaultInventories.put(player.getUniqueId(), vinv);
                    }
                    MainManageData.inventoriesObjects.put(vinv.getInvUuid(), vinv);
                }
            }
            }
        }catch (SQLException exception) {
            if (!MySQL.isConnected()) {
                MySQL.connectMySQL();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable() {
                    @Override
                    public void run() {
                        getDefaultInventory(player);
                    }
                }, 20);
            } else {
                exception.printStackTrace();
                Main.logger.warning("Something went wrong with loading an default Inventory!");
            }
        }
    }
    public static void loadPlayerVirtualInventoriesObjects(Player player) {
        if (!MySQL.isConnected()) {
            MySQL.connectMySQL();
        }
        try {
            PreparedStatement preparedStatement = MySQL.getConnection().prepareStatement("SELECT * FROM "+ virtualinventorytablename +" as p WHERE p.player_uuid = ?");
            preparedStatement.setString(1, String.valueOf(player.getUniqueId()));
            ResultSet resultSet = preparedStatement.executeQuery();
            HashMap<String, VirtualInventory> map = new HashMap<>();
            for (int i = 0; i <= resultSet.getFetchSize(); i++) {
                while (resultSet.next()){
                    if (!resultSet.getBoolean("web")){

                        VirtualInventory vinv = new VirtualInventory();
                        vinv.setInvUuid(resultSet.getString("inventory_uuid"));
                        vinv.setPlayerName(player.getName());
                        vinv.setInvName(resultSet.getString("inventory_name"));
                        vinv.setSize(resultSet.getInt("size"));
                        vinv.setOwnerUuid(player.getUniqueId().toString());
                        vinv.setIsWeb(false);
                        MainManageData.inventoriesObjects.put(vinv.getInvUuid(), vinv);
                        //map.put(resultSet.getString("inventory_uuid"), vinv);
                        if(Objects.equals(vinv.getInvName(), "default")){
                            Main.logger.info("loadPlayerVirtualInventoriesObjects loaded default to playersDefaultInventories");
                            MainManageData.playersDefaultInventories.put(UUID.fromString(vinv.getOwnerUuid()) , vinv);
                        }
                    }
                }
            }

            //what to do with this

        } catch (SQLException exception) {
            if (!MySQL.isConnected()) {
                MySQL.connectMySQL();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable() {
                    @Override
                    public void run() {
                        loadPlayerVirtualInventoriesUuids(player);
                    }
                }, 20);
            } else {
                exception.printStackTrace();
                Main.logger.warning("Something went wrong with loading an Inventory!");
            }
        }
    }

    public static HashMap<String, String> loadAllVirtualInventoriesUuids() {
        if (!MySQL.isConnected()) {
            MySQL.connectMySQL();
        }
        try {
            PreparedStatement preparedStatement = MySQL.getConnection().prepareStatement("SELECT * FROM "+ virtualinventorytablename);
            ResultSet resultSet = preparedStatement.executeQuery();
            HashMap<String, String> map = new HashMap<String, String>();
            //ArrayList<String> array = new ArrayList<String>();
            while (resultSet.next()){
                map.put(resultSet.getString("inventory_uuid"), resultSet.getString("player_name"));
                //array.add(resultSet.getString("inventory_uuid"));

            }
            return map;

            //what to do with this

        } catch (SQLException exception) {
            if (!MySQL.isConnected()) {
                MySQL.connectMySQL();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable() {
                    @Override
                    public void run() {
                        loadAllVirtualInventoriesUuids();
                    }
                }, 20);
            } else {
                exception.printStackTrace();
                Main.logger.warning("Something went wrong with loading an Inventory!");
            }
            return null;
        }
    }
    public static HashMap<String, VirtualInventory> loadAllVirtualInventoriesObjects() {
        if (!MySQL.isConnected()) {
            MySQL.connectMySQL();
        }
        try {
            PreparedStatement preparedStatement = MySQL.getConnection().prepareStatement("SELECT * FROM "+ virtualinventorytablename);
            ResultSet resultSet = preparedStatement.executeQuery();
            HashMap<String, VirtualInventory> map = new HashMap<String, VirtualInventory>();
            //ArrayList<String> array = new ArrayList<String>();
            while (resultSet.next()){
                if (!resultSet.getBoolean("web")){
                    VirtualInventory vinv = new VirtualInventory();
                    vinv.setInvUuid(resultSet.getString("inventory_uuid"));
                    vinv.setPlayerName("player_name");
                    vinv.setInvName(resultSet.getString("inventory_name"));
                    vinv.setSize(resultSet.getInt("size"));
                    vinv.setOwnerUuid(resultSet.getString("player_uuid"));
                    vinv.setIsWeb(false);
                    map.put(resultSet.getString("inventory_uuid"), vinv);
                }
            }
            return map;

            //what to do with this

        } catch (SQLException exception) {
            if (!MySQL.isConnected()) {
                MySQL.connectMySQL();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable() {
                    @Override
                    public void run() {
                        loadAllVirtualInventoriesUuids();
                    }
                }, 20);
            } else {
                exception.printStackTrace();
                Main.logger.warning("Something went wrong with loading an Inventory!");
            }
            return null;
        }
    }
    public static void saveVirtualInventoryInDatabase(String playeruuid, String playername, String uuid, String json, String name, int size, boolean web){
        if (!MySQL.isConnected()) {
            MySQL.connectMySQL();
        }
        if (isInventoryInDB(uuid)) {
            try {
                String statement = "UPDATE " + virtualinventorytablename + " AS p SET p.player_uuid = ?, p.player_name = ?, p.inventory_uuid = ?, p.inventory_name = ?, p.jsondata = ?, p.last_updated = ?, p.size = ?, p.web = ? WHERE p.inventory_uuid = ?";
                PreparedStatement preparedStatement = MySQL.getConnection().prepareStatement(statement);
                preparedStatement.setString(1, playeruuid);
                preparedStatement.setString(2, playername);
                preparedStatement.setString(3, uuid);;
                preparedStatement.setString(4, name);
                preparedStatement.setString(5, json);
                Date dateNow = new Date();
                SimpleDateFormat simpleDateFormat =
                        new SimpleDateFormat("MM.dd.yyyy G 'at' HH:mm:ss z");
                preparedStatement.setString(6, simpleDateFormat.format(dateNow));
                preparedStatement.setInt(7, size);
                preparedStatement.setBoolean(8, web);
                preparedStatement.setString(9, uuid);

            } catch (SQLException exception) {
                if (!MySQL.isConnected()) {
                    MySQL.connectMySQL();
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable() {
                        @Override
                        public void run() {
                            saveVirtualInventoryInDatabase(playeruuid, playername, uuid, json, name,size, web);
                        }
                    }, 20);
                } else {
                    exception.printStackTrace();
                    Main.logger.warning("Something went wrong with saving an Inventory!");
                }
            }
        }
        else {
            generateInventory(playeruuid, playername, uuid,json, name, size, web);
        }

    }
}
