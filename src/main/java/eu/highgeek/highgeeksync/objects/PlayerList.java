package eu.highgeek.highgeeksync.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.utils.ConfigManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PlayerList {

    private final String serverName;
    private final List<String> playerList;


    public PlayerList (){
        this.playerList = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        this.serverName = ConfigManager.getString("chat.servername");
    }



    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static void updatePlayerList(){
        PlayerList playerList = new PlayerList();

        RedisManager.setRedis("server:"+playerList.getServerName()+":playerlist" ,gson.toJson(playerList, PlayerList.class));
    }
}
