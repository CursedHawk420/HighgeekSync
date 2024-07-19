package eu.highgeek.highgeeksync.objects;

import lombok.*;
import jline.internal.Nullable;

import java.util.UUID;

@Setter
@Getter
@Builder
public class Message {
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

    public Message(String uuid, String username, String nickname, String message, String primarygroup, String datetime, String channel, String channelprefix, String source, String servername, String prefix, String suffix, UUID playeruuid){
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
    }


}