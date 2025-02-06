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

import java.util.List;

public class RedisChatListener implements Listener {

    private final ChannelManager channelManager;
    public RedisChatListener(ChannelManager channelManager){
        this.channelManager = channelManager;
    }

    @EventHandler
    public void onRedisChatEvent(AsyncRedisChatEvent event){
        sendAsyncChatMessageToPlayers(event.getMessage());
        HighgeekSync.getInstance().logger.warning("RedisChat event received'");
    }

    public void sendAsyncChatMessageToPlayers(ChatMessage message){

        PacketContainer packetToSend =  MessageBuilder.createChatPacket(message);

        ChatChannel channel = channelManager.getChatChannelFromName(message.getChannel());

        //for (HighgeekPlayer highgeekPlayer : channelManager.channelPlayers.get(channel)) {
        for (HighgeekPlayer highgeekPlayer : channelManager.channelPlayers.get(channel)) {
            sendChatPacket(highgeekPlayer.getPlayer(), packetToSend);
        }

        //todo list players in channel
    }

    public static void sendChatPacket(Player player, PacketContainer packet) {
        try {
            HighgeekSync.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
