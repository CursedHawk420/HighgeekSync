package eu.highgeek.highgeeksync.events;

import eu.highgeek.highgeeksync.objects.Message;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncRedisChatSetEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Message message;

    private final String rawUuid;

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public AsyncRedisChatSetEvent(Message message, String rawUuid){
        this.message = message;
        this.rawUuid = rawUuid;
    }

    public Message getMessage() {
        return this.message;
    }

    public String getRawUuid() {
        return this.rawUuid;
    }

}
