package eu.highgeek.highgeeksync.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RedisNewInventoryEvent extends Event{

    private static final HandlerList HANDLERS = new HandlerList();
    
    private final String rawUuid;
    private final String uuid;

    public RedisNewInventoryEvent(String uuid, String rawUuid){
        this.rawUuid = rawUuid;
        this.uuid = uuid;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }


    public static HandlerList getHandlerList() {
        return HANDLERS;
    }


    public String getRawUuid() {
        return this.rawUuid;
    }

    
    public String getInvUuid() {
        return this.uuid;
    }
}
