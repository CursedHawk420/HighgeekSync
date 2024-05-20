package eu.highgeek.highgeeksync.websync.chat;

import jline.internal.Nullable;
import lombok.*;

import java.util.UUID;


@Getter
@Setter
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
}
