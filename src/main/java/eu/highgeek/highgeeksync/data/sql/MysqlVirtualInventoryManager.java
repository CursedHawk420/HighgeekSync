package eu.highgeek.highgeeksync.data.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.objects.VirtualInventory;
import eu.highgeek.highgeeksync.sync.inventory.InventoryManager;
import eu.highgeek.highgeeksync.utils.ConfigManager;

public class MysqlVirtualInventoryManager {

    public static String virtualinventorytablename = ConfigManager.getString("mysql.virtualinventorytablename");

    public static void loadAllVirtualInventoriesObjects() {
        if (!MySql.isConnected()) {
            MySql.connectMySQL();
        }
        try {
            PreparedStatement preparedStatement = MySql.getConnection().prepareStatement("SELECT * FROM "+ virtualinventorytablename);
            ResultSet resultSet = preparedStatement.executeQuery();
            //ArrayList<String> array = new ArrayList<String>();
            while (resultSet.next()){
                if (!resultSet.getBoolean("web")){
                    VirtualInventory vinv = new VirtualInventory();
                    vinv.setInvUuid(resultSet.getString("inventory_uuid"));
                    vinv.setPlayerName(resultSet.getString("player_name"));
                    vinv.setInvName(resultSet.getString("inventory_name"));
                    vinv.setSize(resultSet.getInt("size"));
                    vinv.setOwnerUuid(resultSet.getString("player_uuid"));
                    vinv.setIsWeb(false);
                    InventoryManager.inventoriesList.add(vinv);
                }
            }

            //what to do with this

        } catch (SQLException exception) {
            if (!MySql.isConnected()) {
                MySql.connectMySQL();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable() {
                    @Override
                    public void run() {
                        loadAllVirtualInventoriesObjects();
                    }
                }, 20);
            } else {
                exception.printStackTrace();
                Main.logger.warning("Something went wrong with loading an Inventory!");
            }
        }
    }

    public static void saveVirtualInventoryInDatabase(String playeruuid, String playername, String uuid, String json, String name, int size, boolean web){
        if (!MySql.isConnected()) {
            MySql.connectMySQL();
        }
        if (isInventoryInDB(uuid)) {
            try {
                String statement = "UPDATE " + virtualinventorytablename + " AS p SET p.player_uuid = ?, p.player_name = ?, p.inventory_uuid = ?, p.inventory_name = ?, p.jsondata = ?, p.last_updated = ?, p.size = ?, p.web = ? WHERE p.inventory_uuid = ?";
                PreparedStatement preparedStatement = MySql.getConnection().prepareStatement(statement);
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
                if (!MySql.isConnected()) {
                    MySql.connectMySQL();
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

    public static void generateInventory(String playeruuid, String playername, String uuid, String jsondata, String name, int size, boolean web) {
        if (!MySql.isConnected()) {
            MySql.connectMySQL();
        }
        if (isInventoryInDB(uuid)) return;
        try {
            PreparedStatement preparedStatement = MySql.getConnection().prepareStatement("INSERT INTO "+ virtualinventorytablename +" (player_uuid, player_name, inventory_uuid, inventory_name, jsondata, last_updated, size, web) VALUES(?,?,?,?,?,?,?,?)");
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
            if (!MySql.isConnected()) {
                MySql.connectMySQL();
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

    public static Boolean isInventoryInDB(String uuid) {
        if (!MySql.isConnected()) {
            MySql.connectMySQL();
        }
        try {
            PreparedStatement tryPreparedStatement = MySql.getConnection().prepareStatement("SELECT p.last_updated FROM "+ virtualinventorytablename +" as p WHERE p.inventory_uuid = ?");
            tryPreparedStatement.setString(1, uuid);
            ResultSet rs = tryPreparedStatement.executeQuery();
            return rs.next();
        } catch (SQLException ignored) {
            return false;
        }
    }
}
