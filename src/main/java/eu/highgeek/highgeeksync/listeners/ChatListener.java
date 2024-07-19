package eu.highgeek.highgeeksync.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.events.AsyncRedisChatSetEvent;
import eu.highgeek.highgeeksync.objects.Message;
import eu.highgeek.highgeeksync.protocollib.WrapperPlayServerChat;
import eu.highgeek.highgeeksync.sync.chat.MessageSender;
import eu.highgeek.highgeeksync.utils.ConfigManager;
import eu.highgeek.highgeeksync.utils.VersionHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.time.LocalDateTime;

public class ChatListener implements Listener {

    public static final String servername = ConfigManager.getString("chat.servername");

    @EventHandler
    public void onGameChatMessage(AsyncPlayerChatEvent event){
        Main.logger.warning("Async onGameChatMessage eventHandler hit");
        String playerUuid = event.getPlayer().getUniqueId().toString();
        String playerName = event.getPlayer().getName();

        //todo fill in correct strings
        String primaryGroup = event.getEventName();


        String time =  LocalDateTime.now().toString().replaceAll(":", "-");

        String channel = "todo";
        String uuid = "chat:"+channel+":"+time+":"+playerName;
        String channelPrefix = "chPrefix";
        String prefix = "playerPrefix";
        String suffix = "playerSuffix";

        sendAsyncChatMessageToPlayers(new Message(uuid, playerName, playerName, event.getMessage(), primaryGroup, time, channel, channelPrefix, "game", servername, prefix, suffix, event.getPlayer().getUniqueId()));

    }


    @EventHandler
    public void onRedisChatMessage(AsyncRedisChatSetEvent event){
        Main.logger.warning("Async onRedisChatMessage eventHandler hit");
        sendAsyncChatMessageToPlayers(event.getMessage());
    }

    public void sendAsyncChatMessageToPlayers(Message message){
        String toSend = message.getMessage();
        MessageSender.createChatPacket(message);
        //todo list players in channel
    }

    public static void sendChatPacket(Player player, PacketContainer packet) {
        //todo send packet to them
        try {
            Main.protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
