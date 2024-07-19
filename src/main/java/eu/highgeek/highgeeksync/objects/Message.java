package eu.highgeek.highgeeksync.objects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

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