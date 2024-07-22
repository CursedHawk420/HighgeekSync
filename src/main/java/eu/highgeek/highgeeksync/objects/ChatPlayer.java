package eu.highgeek.highgeeksync.objects;

import java.util.List;

import org.bukkit.entity.Player;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatPlayer {

    private Player player;
    private List<ChatChannel> joinedChannels;
    private ChatChannel channelOut;

    public ChatPlayer(Player player, List<ChatChannel> joinedChannels, ChatChannel channelOut){
        this.player = player;
        this.joinedChannels = joinedChannels;
        this.channelOut = channelOut;
    }

    public ChatPlayer(){
        
    }
}
