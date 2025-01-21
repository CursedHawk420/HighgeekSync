package eu.highgeek.highgeeksync.listeners;

import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.objects.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.time.Instant;
import java.util.UUID;

public class DeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        try {
            Player killed = event.getEntity().getPlayer();
            Player killer = event.getEntity().getPlayer().getKiller();
            sendDeathMessage(killed.getName() + " has been killed by " + killer.getName() + ".");
        }catch (Exception exception){

            Player killed = event.getEntity().getPlayer();
            sendDeathMessage(killed.getName() + " has been killed.");
        }
    }

    public void sendDeathMessage(String message){
        String time =  Instant.now().toString();
        String uuid = "chat:deaths:"+time.replaceAll(":", "-")+":server";
        RedisManager.addChatEntry(new Message(uuid, "Grim-Reaper", "Grim-Reaper", message, "sa", time, "deaths", "&7Death", "game", ChatListener.servername, "&7", "&f", UUID.fromString("00000000-0000-0000-0000-000000000000"), ChatListener.prettyServerName));
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {

    }
}
