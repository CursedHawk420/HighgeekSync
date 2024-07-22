package eu.highgeek.highgeeksync.listeners;

import java.time.LocalDateTime;
import java.util.List;

import eu.highgeek.highgeeksync.common.Common;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.utils.PlaceholderParser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.comphenix.protocol.events.PacketContainer;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.events.AsyncRedisChatSetEvent;
import eu.highgeek.highgeeksync.objects.ChatChannel;
import eu.highgeek.highgeeksync.objects.ChatPlayer;
import eu.highgeek.highgeeksync.objects.Message;
import eu.highgeek.highgeeksync.sync.chat.ChannelManager;
import eu.highgeek.highgeeksync.sync.chat.MessageSender;
import eu.highgeek.highgeeksync.utils.ConfigManager;

public class ChatListener implements Listener {

    public static final String servername = ConfigManager.getString("chat.servername");
    public static final String prettyServerName = ConfigManager.getString("chat.prettyservername");

    @EventHandler
    public void onGameChatMessage(AsyncPlayerChatEvent event){
        event.setCancelled(true);
        Main.logger.warning("Async onGameChatMessage eventHandler hit");
        String playerUuid = event.getPlayer().getUniqueId().toString();
        String playerName = event.getPlayer().getName();


        String time =  LocalDateTime.now().toString();

        ChatChannel chatChannel = ChannelManager.getChatChannelFromName(Common.playerSettings.get(event.getPlayer()).channelOut);
        String uuid = "chat:"+chatChannel.getName()+":"+time.replaceAll(":", "-")+":"+playerName;
        String channelPrefix = chatChannel.getPrefix();
        String prefix = PlaceholderParser.parsePlaceholders("%vault_prefix%", event.getPlayer());
        String suffix = PlaceholderParser.parsePlaceholders("%vault_suffix%", event.getPlayer());;
        String primaryGroup = PlaceholderParser.parsePlaceholders("%luckperms_primary_group_name%", event.getPlayer());


        RedisManager.addChatEntry(new Message(uuid, playerName, playerName, event.getMessage(), primaryGroup, time, chatChannel.getName(), channelPrefix, "game", servername, prefix, suffix, event.getPlayer().getUniqueId(), prettyServerName));
        //sendAsyncChatMessageToPlayers(new Message(uuid, playerName, playerName, event.getMessage(), primaryGroup, time, chatChannel.getName(), channelPrefix, "game", servername, prefix, suffix, event.getPlayer().getUniqueId(), prettyServerName));

    }


    @EventHandler
    public void onRedisChatMessage(AsyncRedisChatSetEvent event){
        Main.logger.warning("Async onRedisChatMessage eventHandler hit");
        sendAsyncChatMessageToPlayers(event.getMessage());
    }

    public void sendAsyncChatMessageToPlayers(Message message){

        PacketContainer packetToSend =  MessageSender.createChatPacket(message);

        ChatChannel channel = ChannelManager.getChatChannelFromName(message.getChannel());
        List<ChatPlayer> chatPlayers = ChannelManager.channelPlayers.get(channel);

        for (ChatPlayer chatPlayer : chatPlayers) {
            sendChatPacket(chatPlayer.getPlayer(), packetToSend);
        }

        //todo list players in channel
    }

    public static void sendChatPacket(Player player, PacketContainer packet) {
        try {
            Main.protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
