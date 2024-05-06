package eu.highgeek.highgeeksync.websync.chat;

import lombok.*;


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
}
