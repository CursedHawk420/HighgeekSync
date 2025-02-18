package eu.highgeek.highgeeksync.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Setter
@Getter
@Builder
public class ChatMessage {
    private String uuid;
    private String username;
    private String nickname;
    private String message;
    private String primarygroup;
    private String datetime;
    private String channel;
    private String channelprefix;
    private String source;
    private String servername;
    @Nullable
    private String prefix;
    @Nullable
    private String suffix;
    @Nullable
    private UUID playeruuid;
    private String prettyservername;

    public ChatMessage(String uuid, String username, String nickname, String message, String primarygroup, String datetime, String channel, String channelprefix, String source, String servername, @Nullable String prefix, @Nullable String suffix, @Nullable UUID playeruuid, String prettyservername){
        this.uuid = uuid;
        this.username = username;
        this.nickname = nickname;
        this.message = message;
        this.primarygroup = primarygroup;
        this.datetime = datetime;
        this.channel = channel;
        this.channelprefix = channelprefix;
        this.source = source;
        this.servername = servername;
        this.prefix = prefix;
        this.suffix = suffix;
        this.playeruuid = playeruuid;
        this.prettyservername = prettyservername;
    }
}
