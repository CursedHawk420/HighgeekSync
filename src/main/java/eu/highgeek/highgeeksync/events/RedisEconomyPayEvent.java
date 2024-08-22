package eu.highgeek.highgeeksync.events;

import eu.highgeek.highgeeksync.objects.Message;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RedisEconomyPayEvent extends Event {

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

    public RedisEconomyPayEvent(String json, String rawUuid, boolean isAsync){
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
