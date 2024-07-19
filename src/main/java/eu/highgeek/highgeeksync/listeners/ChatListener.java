package eu.highgeek.highgeeksync.listeners;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.events.AsyncRedisChatSetEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onGameChatMessage(AsyncPlayerChatEvent event){
        Main.logger.warning("Async onGameChatMessage eventHandler hit");
    }


    @EventHandler
    public void onRedisChatMessage(AsyncRedisChatSetEvent event){
        Main.logger.warning("Async onRedisChatMessage eventHandler hit");
    }
}
