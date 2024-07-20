package eu.highgeek.highgeeksync.objects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatChannel {
    public String name;
    public String prefix;
    public boolean isLocal;
    public boolean isDefault;
}
