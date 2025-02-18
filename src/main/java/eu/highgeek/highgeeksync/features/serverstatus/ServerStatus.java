package eu.highgeek.highgeeksync.features.serverstatus;

import com.google.gson.Gson;
import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.models.PlayerSettings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ServerStatus implements Listener {

    private final transient Gson gson;
    private final transient RedisManager redisManager;

    private final String serverName;
    private String status;
    private boolean visible;
    private int position;
    private final List<String> playerList;

    public ServerStatus(RedisManager redisManager){
        this.playerList = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        this.serverName = HighgeekSync.getInstance().config.getServerName();
        this.status = "online";
        this.visible = HighgeekSync.getInstance().config.getServerListVisible();
        this.position = HighgeekSync.getInstance().config.getServerListPos();

        this.redisManager = redisManager;
        this.gson = redisManager.gson;

        updateRedis();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        this.playerList.add(event.getPlayer().getName());
        updateRedis();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        this.playerList.remove(event.getPlayer().getName());
        updateRedis();
    }


    public void onShutDown(){
        this.playerList.clear();
        this.status = "offline";
        updateRedis();
    }

    private void updateRedis(){
        redisManager.setStringRedis("server:"+getServerName()+":playerlist" ,gson.toJson(this, ServerStatus.class));
    }
}
