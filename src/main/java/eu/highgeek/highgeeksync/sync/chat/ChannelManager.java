package eu.highgeek.highgeeksync.sync.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import eu.highgeek.highgeeksync.menus.ChannelSelector;
import org.bukkit.entity.Player;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.common.Common;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.objects.ChatChannel;
import eu.highgeek.highgeeksync.objects.ChatPlayer;
import eu.highgeek.highgeeksync.objects.PlayerSettings;

public class ChannelManager {

    public static List<ChatPlayer> chatPlayers = new ArrayList<>();

    public static List<ChatChannel> chatChannels = new ArrayList<>();
    //public static List<String> defaultChannels = new ArrayList<>();

    public static HashMap<String, ChannelSelector> openedSelectors = new HashMap<>();

    public static HashMap<ChatChannel, List<ChatPlayer>> channelPlayers = new HashMap<>();

    public static void onPlayerJoin(PlayerSettings playerSettings, Player player){
        ChatPlayer chatPlayer = new ChatPlayer();
        chatPlayer.setPlayerName(player.getName());
        chatPlayer.setPlayer(player);
        joinPlayerToChannels(chatPlayer, playerSettings);
    }

    public static void joinPlayerToChannels(ChatPlayer chatPlayer, PlayerSettings playerSettings){
        if (chatPlayers.contains(chatPlayer)){
            chatPlayers.remove(chatPlayer);
            for(ChatChannel chatChannel : chatPlayer.getJoinedChannels()){
                channelPlayers.get(chatChannel).remove(chatPlayer);
            }
        }

        List<ChatChannel> playerChannels = new ArrayList<>();
        Main.logger.warning("Player channels: ");
        for (String stringChannel : playerSettings.joinedChannels) {
            ChatChannel playChannel = chatChannels.stream()
            .filter(s -> s.name.equals(stringChannel))
            .findAny()
            .orElse(null);
            playerChannels.add(playChannel);
            channelPlayers.get(playChannel).add(chatPlayer);

            Main.logger.warning("Name: " + playChannel.name + ", Prefix: " + playChannel.prefix + ", isDefault: " + playChannel.isDefault + ", isLocal: " + playChannel.isLocal);
        }
        chatPlayer.setJoinedChannels(playerChannels);
        chatPlayers.add(chatPlayer);
    }

    public static ChatChannel getChatChannelFromName(String name){
        ChatChannel channel = chatChannels.stream()
                .filter(inv -> inv.name.equals(name))
                .findAny()
                .orElse(null);
                
        return channel;
    }

    public static ChatPlayer getChatPlayer(String playerName){
        ChatPlayer chatPlayer = chatPlayers.stream()
        .filter(s -> s.getPlayerName().equals(playerName))
        .findAny()
        .orElse(null);

        return chatPlayer;
    }

    public static List<ChatChannel> getDefaultChatChannels(){
        List<ChatChannel> channels = chatChannels.stream()
        .filter(channel -> channel.isDefault())
        .collect(Collectors.toList());
        
        return channels;
    }

    public static void onPlayerQuit(Player player){
        //todo unload all player related vars
        ChatPlayer chatPlayer = getChatPlayer(player.getName());
        
        chatPlayers.remove(chatPlayer);
        
        for (ChatChannel chatChannel : chatPlayer.getJoinedChannels()) {
            channelPlayers.get(chatChannel).remove(chatPlayer);
        }
    }

    /*public static void joinPlayerToChannel(ChatPlayer chatPlayer, ChatChannel channel){
        channelPlayers.get(channel).add(chatPlayer);
        chatPlayer.getJoinedChannels().add(channel);

        Common.playerSettings.get(chatPlayer.getPlayerName()).joinedChannels.add(channel.name);
        RedisManager.setPlayerSettings(Common.playerSettings.get(chatPlayer.getPlayerName()));

    }*/

    /*public static void disconnectPlayerFromChannel(ChatPlayer chatPlayer, ChatChannel chatChannel){
        chatPlayer.getJoinedChannels().remove(chatChannel);
        channelPlayers.get(chatChannel).remove(chatPlayer);

        Common.playerSettings.get(chatPlayer.getPlayerName()).joinedChannels.remove(chatChannel.name);
        RedisManager.setPlayerSettings(Common.playerSettings.get(chatPlayer.getPlayerName()));
    }

    public static void setChannelOut(ChatPlayer chatPlayer, ChatChannel chatChannel){
        Common.playerSettings.get(chatPlayer.getPlayerName()).channelOut = chatChannel.getName();
        RedisManager.setPlayerSettings(Common.playerSettings.get(chatPlayer.getPlayerName()));

        if(!checkPlayerInChannel(chatPlayer, chatChannel)){
            joinPlayerToChannel(chatPlayer, chatChannel);
        }
    }

    public static boolean checkPlayerInChannel(ChatPlayer chatPlayer, ChatChannel chatChannel){
        return Common.playerSettings.get(chatPlayer.getPlayerName()).joinedChannels.contains(chatChannel.getName());
    }*/
}
