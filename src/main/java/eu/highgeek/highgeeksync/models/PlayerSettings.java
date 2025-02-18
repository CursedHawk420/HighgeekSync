package eu.highgeek.highgeeksync.models;

import eu.highgeek.highgeeksync.HighgeekSync;

import java.util.ArrayList;
import java.util.List;

public class PlayerSettings {

    public String playerName;
    public String playerUuid;
    public List<String> joinedChannels;
    public String channelOut;
    public boolean hasConnectedDiscord;
    public List<String> mutedPlayers;

    public transient HighgeekPlayer player;

    public PlayerSettings(String playerName, String playerUuid, List<String> joinedChannels, String channelOut, boolean hasConnectedDiscord, List<String> mutedPlayers){
        this.playerName = playerName;
        this.playerUuid = playerUuid;
        this.joinedChannels = joinedChannels;
        this.channelOut = channelOut;
        this.hasConnectedDiscord = hasConnectedDiscord;
        this.mutedPlayers = mutedPlayers;
    }


    public List<ChatChannel> getJoinedChannels(){
        List<ChatChannel> channels = new ArrayList<>();
        for (String stringChannel : this.joinedChannels) {
            ChatChannel playChannel = HighgeekSync.getChannelManager().chatChannels.stream()
                    .filter(s -> s.name.equals(stringChannel))
                    .findAny()
                    .orElse(null);
            channels.add(playChannel);
            HighgeekSync.getChannelManager().channelPlayers.get(playChannel);
            if(!HighgeekSync.getChannelManager().channelPlayers.get(playChannel).contains(player)){
                HighgeekSync.getChannelManager().channelPlayers.get(playChannel).addLast(player);
            }
        }
        return channels;
    }

    public ChatChannel getChannelOut(){
        return HighgeekSync.getChannelManager().chatChannels.stream()
            .filter(s -> s.name.equals(this.channelOut))
            .findAny()
            .orElse(null);
    }
}

