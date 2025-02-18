package eu.highgeek.highgeeksync.listeners;

import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.features.chat.ChannelManager;
import eu.highgeek.highgeeksync.models.ChatChannel;
import eu.highgeek.highgeeksync.models.ChatMessage;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ChatListener implements Listener {
    public static final String servername =  HighgeekSync.getInstance().config.getServerName();
    public static final String prettyServerName = HighgeekSync.getInstance().config.getPrettyServerName();


    private ChannelManager channelManager;
    private final RedisManager redisManager;
    public ChatListener(RedisManager redisManager, ChannelManager channelManager){
        this.channelManager = channelManager;
        this.redisManager = redisManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onGameChatMessage(AsyncPlayerChatEvent event){
        //sendAsyncChatMessageToPlayers(new Message(uuid, playerName, playerName, event.getMessage(), primaryGroup, time, chatChannel.getName(), channelPrefix, "game", servername, prefix, suffix, event.getPlayer().getUniqueId(), prettyServerName));
        setRedisMessageAsync(event);
        event.setCancelled(true);
    }
    public void setRedisMessageAsync(AsyncPlayerChatEvent event){
        Bukkit.getScheduler().runTaskAsynchronously(HighgeekSync.getInstance(), new Runnable() {
            @Override
            public void run() {
                String playerName = event.getPlayer().getName();
                String time =  Instant.now().toString();

                ChatChannel chatChannel = HighgeekSync.getInstance().getHighgeekPlayers().get(event.getPlayer().getName()).getChannelOut();

                String uuid = "chat:"+chatChannel.getName()+":"+time.replaceAll(":", "-")+":"+playerName;
                String channelPrefix = chatChannel.getPrefix();

                String prefix = PlaceholderAPI.setPlaceholders(event.getPlayer(), "%vault_prefix%");
                String suffix = PlaceholderAPI.setPlaceholders(event.getPlayer(), "%vault_suffix%");;
                String primaryGroup = PlaceholderAPI.setPlaceholders(event.getPlayer(),"%luckperms_primary_group_name%");

                redisManager.addChatEntry(new ChatMessage(uuid, playerName, playerName, event.getMessage(), primaryGroup, time, chatChannel.getName(), channelPrefix, "game", servername, prefix, suffix, event.getPlayer().getUniqueId(), prettyServerName));
            }
        });
    }
}
