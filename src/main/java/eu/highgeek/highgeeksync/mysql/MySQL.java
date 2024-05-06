package eu.highgeek.highgeeksync.mysql;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.util.ConfigManager;
import org.bukkit.Bukkit;

import java.sql.*;

public class MySQL {

    public static String host = ConfigManager.getString("mysql.host");
    public static String port = ConfigManager.getString("mysql.port");
    public static String database = ConfigManager.getString("mysql.database");
    public static String username = ConfigManager.getString("mysql.username");
    public static String password = ConfigManager.getString("mysql.password");
    public static String mysqltablename = ConfigManager.getString("mysql.synctablename");
    public static String virtualinventorytablename = ConfigManager.getString("mysql.virtualinventorytablename");
    public static Connection connection;

    // Connect to Database
    public static void connectMySQL() {
        if (!isConnected()) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                Main.logger.info("§aConnected to the MySQL");
            } catch (SQLException ex) {
                Main.logger.severe("No valid MySQL Credentials is set in Config!\n Disabling Plugin!");
                Bukkit.getPluginManager().disablePlugin(Main.main);
            }
        }
    }

    // Disconnect from Database
    public static void disconnectMySQL() {
        if (isConnected()) {
            try {
                connection.close();
                Main.logger.info("§cDisconnected from the MySQL");
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    // Setting up the Database
    public static void registerMySQL() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SHOW TABLES LIKE '"+mysqltablename+"'");
        ResultSet rs = preparedStatement.executeQuery();
        if (!rs.next()) {
            PreparedStatement prepareStatementOne = MySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS "+mysqltablename+" (player_uuid VARCHAR(100) NOT NULL, player_name VARCHAR(16), inventory TEXT, gamemode VARCHAR(18), health INT(10), food INT(10), enderchest TEXT, exp INT(255), last_joined VARCHAR(255), effects LONGTEXT, advancements LONGTEXT, statistics LONGTEXT, PRIMARY KEY (player_uuid))");
            prepareStatementOne.executeUpdate();
        }
        preparedStatement = connection.prepareStatement("SHOW TABLES LIKE '"+ virtualinventorytablename +"'");
        rs = preparedStatement.executeQuery();
        if (!rs.next()) {
            PreparedStatement prepareStatementOne = MySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS "+ virtualinventorytablename +" (player_uuid VARCHAR(100), player_name VARCHAR(16), inventory_uuid VARCHAR(100) NOT NULL, inventory_name VARCHAR(100), jsondata LONGTEXT, last_updated VARCHAR(255), PRIMARY KEY (inventoryname))");
            prepareStatementOne.executeUpdate();
        }
    }

    // If the Server is connected to the MySQL
    public static boolean isConnected() {
        if (connection == null) {
            return false;
        }
        try {
            if (connection.isClosed()) {
                return false;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    // Get the Connection
    public static Connection getConnection() {
        return connection;
    }
}
