package eu.highgeek.highgeeksync.sync.info;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.objects.PlayerList;

public class PlayerListUpdater {

    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();


    public static void updatePlayerList(){
        PlayerList playerList = new PlayerList();

        RedisManager.setRedis("server:"+playerList.getServerName()+":playerlist" ,gson.toJson(playerList, PlayerList.class));
    }
}
