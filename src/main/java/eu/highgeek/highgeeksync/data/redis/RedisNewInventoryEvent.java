package eu.highgeek.highgeeksync.data.redis;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RedisNewInventoryEvent extends Event{

    private static final HandlerList HANDLERS = new HandlerList();
    
    private final String rawmessage;
    private final String uuid;

    public RedisNewInventoryEvent(String uuid, String rawUuid){
        this.rawmessage = rawUuid;
        this.uuid = uuid;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    
    public String getRawUuid() {
        return this.rawmessage;
    }

    
    public String getInvUuid() {
        return this.uuid;
    }
}
