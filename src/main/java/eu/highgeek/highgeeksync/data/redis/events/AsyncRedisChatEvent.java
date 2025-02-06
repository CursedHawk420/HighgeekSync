package eu.highgeek.highgeeksync.data.redis.events;

import eu.highgeek.highgeeksync.models.ChatMessage;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncRedisChatEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Getter
    private final ChatMessage message;

    public AsyncRedisChatEvent(ChatMessage message, boolean isAsync){
        super(isAsync);
        this.message = message;
    }
}
