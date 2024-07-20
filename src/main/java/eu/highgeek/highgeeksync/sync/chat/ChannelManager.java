package eu.highgeek.highgeeksync.sync.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import eu.highgeek.highgeeksync.objects.ChatChannel;
import eu.highgeek.highgeeksync.objects.ChatPlayer;
import eu.highgeek.highgeeksync.objects.PlayerSettings;

public class ChannelManager {

    public static List<ChatPlayer> chatPlayers = new ArrayList<>();

    public static List<ChatChannel> chatChannels = new ArrayList<>();
    public static List<String> defaultChannels = new ArrayList<>();

    public static HashMap<ChatChannel, List<ChatPlayer>> channelPlayers = new HashMap<>();

    public static void onPlayerJoin(PlayerSettings playerSettings, Player player){
        ChatPlayer chatPlayer = new ChatPlayer();
        chatPlayer.setPlayer(player);
        List<ChatChannel> playerChannels = new ArrayList<>();

        for (String stringChannel : playerSettings.joinedChannels) {
            ChatChannel playChannel = chatChannels.stream()
            .filter(s -> s.name.equals(stringChannel))
            .findAny()
            .orElse(null);
            playerChannels.add(playChannel);
            channelPlayers.get(playChannel).add(chatPlayer);
        }
        chatPlayer.setJoinedChannels(playerChannels);
    }

    public static ChatChannel getChatChannelFromName(String name){
        ChatChannel channel = chatChannels.stream()
                .filter(inv -> inv.name.equals(name))
                .findAny()
                .orElse(null);
                
        return channel;
    }

    public static ChatPlayer getChatPlayer(Player player){
        ChatPlayer chatPlayer = chatPlayers.stream()
        .filter(s -> s.getPlayer().equals(player))
        .findAny()
        .orElse(null);

        return chatPlayer;
    }

    public static void joinPlayerToDefaultChannels(ChatPlayer player){
        for (ChatChannel chatChannel : getDefaultChatChannels()) {
            joinPlayerToChannel(player, chatChannel);
        }
    }

    public static List<ChatChannel> getDefaultChatChannels(){
        List<ChatChannel> channels = chatChannels.stream()
        .filter(channel -> channel.isDefault())
        .collect(Collectors.toList());
        
        return channels;
    }

    public static void joinPlayerToChannel(ChatPlayer player, ChatChannel channel){
        channelPlayers.get(channel).add(player);

    }
}
