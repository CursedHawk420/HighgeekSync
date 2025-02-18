package eu.highgeek.highgeeksync.listeners;

import com.comphenix.protocol.events.PacketContainer;
import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.redis.RedisSubscriber;
import eu.highgeek.highgeeksync.data.redis.events.AsyncRedisChatEvent;
import eu.highgeek.highgeeksync.features.chat.ChannelManager;
import eu.highgeek.highgeeksync.features.chat.MessageBuilder;
import eu.highgeek.highgeeksync.models.ChatChannel;
import eu.highgeek.highgeeksync.models.ChatMessage;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RedisChatListener implements Listener {

    private final ChannelManager channelManager;
    public RedisChatListener(ChannelManager channelManager){
        this.channelManager = channelManager;
    }

    @EventHandler
    public void onRedisChatEvent(AsyncRedisChatEvent event){
        sendAsyncChatMessageToPlayers(event.getMessage());
    }

    public void sendAsyncChatMessageToPlayers(ChatMessage message){
        HighgeekSync.getInstance().logger.info("Chat: " + message.getNickname() + ": " + message.getMessage());
        PacketContainer packetToSend =  MessageBuilder.createChatPacket(message);
        ChatChannel channel = channelManager.getChatChannelFromName(message.getChannel());
        for (HighgeekPlayer highgeekPlayer : channelManager.channelPlayers.get(channel)) {
            if(!highgeekPlayer.getPlayerSettings().mutedPlayers.contains(message.getNickname())){
                sendChatPacket(highgeekPlayer.getPlayer(), packetToSend);
            }
        }
    }

    public static void sendChatPacket(Player player, PacketContainer packet) {
        try {
            HighgeekSync.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
