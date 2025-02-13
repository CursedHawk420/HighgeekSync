package eu.highgeek.highgeeksync.features.virtualinventories;

import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.sql.entities.VirtualInventories;
import eu.highgeek.highgeeksync.features.adapters.ItemStackAdapter;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;

public class VirtualInventoryHolder implements InventoryHolder {

    @Getter
    private String uuid;
    private Inventory inv;
    @Getter
    private VirtualInventories vinv;
    @Getter
    private HighgeekPlayer player;
    @Getter
    private String invPrefix;

    public VirtualInventoryHolder(String uuid, VirtualInventories vinv, HighgeekPlayer player) {
        this.uuid = uuid;
        this.vinv = vinv;
        this.player = player;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}
