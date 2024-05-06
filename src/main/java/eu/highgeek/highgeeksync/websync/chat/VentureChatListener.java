package eu.highgeek.highgeeksync.websync.chat;

import eu.highgeek.highgeeksync.Main;
import mineverse.Aust1n46.chat.api.events.VentureChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class VentureChatListener implements Listener {
    @EventHandler
    public void onVentureChatEvent(VentureChatEvent event){
        String chat = event.getChat();
        Main.logger.warning("VentureChatListener: onVentureChatEvent triggered ");
        Main.logger.warning(chat);
    }
}
