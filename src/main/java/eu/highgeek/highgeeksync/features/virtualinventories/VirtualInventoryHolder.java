package eu.highgeek.highgeeksync.features.virtualinventories;

import com.comphenix.protocol.PacketType;
import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.sql.entities.VirtualInventories;
import eu.highgeek.highgeeksync.features.adapters.ItemStackAdapter;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public class VirtualInventoryHolder implements InventoryHolder {

    @Getter
    private final String uuid;
    private Inventory inv;
    @Getter
    private final VirtualInventories vinv;
    @Getter
    private final Player player;
    @Getter
    private String invPrefix;

    public VirtualInventoryHolder(String uuid, VirtualInventories vinv, Player player, String invPrefix) {
        this.uuid = uuid;
        this.vinv = vinv;
        this.player = player;
        this.invPrefix = invPrefix;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
