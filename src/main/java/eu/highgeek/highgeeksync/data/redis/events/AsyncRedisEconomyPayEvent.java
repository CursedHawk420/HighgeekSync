package eu.highgeek.highgeeksync.data.redis.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncRedisEconomyPayEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String json;

    private final String rawUuid;

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public AsyncRedisEconomyPayEvent(String json, String rawUuid, boolean isAsync){
        super(isAsync);

        this.json = json;
        this.rawUuid = rawUuid;
    }

    public String getMessage() {
        return this.json;
    }

    public String getRawUuid() {
        return this.rawUuid;
    }
}

