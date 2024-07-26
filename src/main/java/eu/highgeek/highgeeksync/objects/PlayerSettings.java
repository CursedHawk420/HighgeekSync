package eu.highgeek.highgeeksync.objects;

import java.util.List;

public class PlayerSettings {

    public String playerName;
    public String playerUuid;
    public List<String> joinedChannels;
    public String channelOut;
    public boolean hasConnectedDiscord;
    public List<String> mutedPlayers;

    public PlayerSettings(String playerName, String playerUuid, List<String> joinedChannels, String channelOut, boolean hasConnectedDiscord, List<String> mutedPlayers){
        this.playerName = playerName;
        this.playerUuid = playerUuid;
        this.joinedChannels = joinedChannels;
        this.channelOut = channelOut;
        this.hasConnectedDiscord = hasConnectedDiscord;
        this.mutedPlayers = mutedPlayers;
    }
}
