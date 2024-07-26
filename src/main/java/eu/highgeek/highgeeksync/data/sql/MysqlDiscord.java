package eu.highgeek.highgeeksync.data.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.objects.StatusCode;
import eu.highgeek.highgeeksync.utils.ConfigManager;

public class MysqlDiscord {

    public static String discordaccountstable = ConfigManager.getString("mysql.discordtableaccounts");
    public static String discordcodestable = ConfigManager.getString("mysql.discordtablecodes");


    public static StatusCode saveLinkCode(String playeruuid, String linkCode, long expiration) {
        if (!MySql.isConnected()) {
            MySql.connectMySQL();
        }
        //if (isInventoryInDB(uuid)) return;
        try {
            PreparedStatement preparedStatement = MySql.getConnection().prepareStatement("INSERT INTO "+ discordcodestable +" (code, uuid, expiration) VALUES(?,?,?)");
            preparedStatement.setString(1, linkCode);
            preparedStatement.setString(2, playeruuid);
            preparedStatement.setLong(3, expiration);
            preparedStatement.executeUpdate();
            return new StatusCode("Your linking code is: " + linkCode);
        } catch (SQLException exception) {
            if (!MySql.isConnected()) {
                MySql.connectMySQL();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable() {
                    @Override
                    public void run() {
                        saveLinkCode(playeruuid, linkCode, expiration);
                    }
                }, 20);
            } else {
                exception.printStackTrace();
                Main.logger.warning("Something went wrong with saving discord link code for player \""+playeruuid+"\"!");
            }
            return new StatusCode("MysqlDiscord.saveLinkCode()" , "Saving new discord link code in database failed. ", ExceptionUtils.getStackTrace(exception), playeruuid);
        }catch (Exception exception){
            return new StatusCode("MysqlDiscord.saveLinkCode()" , "Saving new discord link code in database failed. ", ExceptionUtils.getStackTrace(exception), playeruuid);
        }
    }

    public static String getLinkingCode(Player player){
        if (!MySql.isConnected()) {
            MySql.connectMySQL();
        }
        try {
            PreparedStatement preparedStatement = MySql.getConnection().prepareStatement("SELECT * FROM "+ discordcodestable +" as p WHERE p.uuid = ?");
            preparedStatement.setString(1, String.valueOf(player.getUniqueId()));
            ResultSet resultSet = preparedStatement.executeQuery();
            for (int i = 0; i <= resultSet.getFetchSize(); i++) {
                while (resultSet.next()){
                    return resultSet.getString("code");
                }
            }
            return null;
            //what to do with this

        } catch (SQLException exception) {
            if (!MySql.isConnected()) {
                MySql.connectMySQL();
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable() {
                    @Override
                    public void run() {
                        getLinkingCode(player);
                    }
                }, 20);
            } else {
                exception.printStackTrace();
                Main.logger.warning("Something went wrong with loading linking code!");
            }
            return null;
        }
    }

}
