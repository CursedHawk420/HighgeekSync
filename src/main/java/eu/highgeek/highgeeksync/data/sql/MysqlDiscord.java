package eu.highgeek.highgeeksync.data.sql;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.objects.StatusCode;
import eu.highgeek.highgeeksync.utils.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
            return new StatusCode("MysqlDiscord.saveLinkCode()" , "Saving new discord link code in database failed. ", exception.getStackTrace().toString(), playeruuid);
        }catch (Exception exception){
            return new StatusCode("MysqlDiscord.saveLinkCode()" , "Saving new discord link code in database failed. ", exception.getStackTrace().toString(), playeruuid);
        }
    }
}
