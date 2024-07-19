package eu.highgeek.highgeeksync.objects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
@Builder
public class ChatPlayer {

    private Player player;
    private ChatChannel currentChannel;
}
