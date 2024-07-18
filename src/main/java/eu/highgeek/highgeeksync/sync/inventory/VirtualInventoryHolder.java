package eu.highgeek.highgeeksync.sync.inventory;


import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import eu.highgeek.highgeeksync.objects.VirtualInventory;

public class VirtualInventoryHolder implements InventoryHolder {
    private String uuid;
    private Inventory inv;
    private VirtualInventory vinv;

    public VirtualInventoryHolder(String uuid, VirtualInventory vinv) {
        this.uuid = uuid;
        this.vinv = vinv;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public String getInventoryUuid() {
        return uuid;
    }
    public VirtualInventory getVirtualInventoryUuid() {
        return vinv;
    }
}
