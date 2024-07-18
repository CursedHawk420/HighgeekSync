package eu.highgeek.highgeeksync.data.redis;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RedisInventorySetEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String rawmessage;
    private final String uuid;
    private final int slot;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public RedisInventorySetEvent(String rawmessage, String uuid, int slot) {
        this.rawmessage = rawmessage;
        this.uuid = uuid;
        this.slot =  slot;
    }


    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public String getRawMessage() {
        return this.rawmessage;
    }

    public String getInvUuid() {
        return this.uuid;
    }

    public int getSlotId() {
        return this.slot;
    }

}