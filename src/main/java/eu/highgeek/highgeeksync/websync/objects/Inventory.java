package eu.highgeek.highgeeksync.websync.objects;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Inventory {
    private String holderName;
    private UUID playerUuid;
    @Nullable
    private String inventoryDisplayName;
    @Nullable
    private String inventoryName;
    private int slots;
    @Nullable
    private Item[] items;

    public Inventory(String holderName, UUID playerUuid,String inventoryDisplayName, String inventoryName, int slots, Item[] items) {
        this.holderName = holderName;
        this.playerUuid = playerUuid;
        this.inventoryDisplayName = inventoryDisplayName;
        this.inventoryName = inventoryName;
        this.slots = slots;
        this.items = items;
    }
    public String getHolderName() {
        return holderName;
    }
    public UUID getPlayerUuid() {
        return playerUuid;
    }
    public String getInventoryDisplayName() {
        return inventoryDisplayName;
    }
    public String getInventoryName() {
        return inventoryName;
    }
    public int getSlots() {
        return slots;
    }
    public Item[] getItems() {
        return items;
    }

}
