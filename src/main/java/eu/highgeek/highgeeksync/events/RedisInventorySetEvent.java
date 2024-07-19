package eu.highgeek.highgeeksync.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RedisInventorySetEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String rawUuid;
    private final String uuid;
    private final int slot;

    public RedisInventorySetEvent(String rawUuid, String uuid, int slot) {
        this.rawUuid = rawUuid;
        this.uuid = uuid;
        this.slot =  slot;
    }


    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }


    public String getRawMessage() {
        return this.rawUuid;
    }

    public String getInvUuid() {
        return this.uuid;
    }

    public int getSlotId() {
        return this.slot;
    }

}