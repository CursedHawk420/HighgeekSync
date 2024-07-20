package eu.highgeek.highgeeksync.objects;

import java.util.List;

public class PlayerSettings {

    public String playerName;
    public String playerUuid;
    public List<String> joinedChannels;

    public PlayerSettings(String playerName, String playerUuid, List<String> joinedChannels){
        this.playerName = playerName;
        this.playerUuid = playerUuid;
        this.joinedChannels = joinedChannels;
    }
}
