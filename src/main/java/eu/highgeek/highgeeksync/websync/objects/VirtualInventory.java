package eu.highgeek.highgeeksync.websync.objects;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class VirtualInventory {
    public String OwnerUuid;
    public String PlayerName;
    public String InvUuid;
    public String InvName;
    public int Size;
    public boolean IsWeb;
}
