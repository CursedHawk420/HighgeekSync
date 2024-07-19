package eu.highgeek.highgeeksync.objects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ChatChannel {
    private String name;
    private String prefix;
    private boolean isLocal;

    public List<ChatPlayer> joinedPlayers;
}
