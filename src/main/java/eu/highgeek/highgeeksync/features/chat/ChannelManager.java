package eu.highgeek.highgeeksync.features.chat;

import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.models.ChatChannel;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import eu.highgeek.highgeeksync.models.PlayerSettings;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChannelManager {
    private final RedisManager redisManager;

    public List<ChatChannel> chatChannels = new ArrayList<>();

    public HashMap<ChatChannel, List<HighgeekPlayer>> channelPlayers = new HashMap<>();

    public ChannelManager(RedisManager redisManager){
        this.redisManager = redisManager;
        initChannels();
    }

    private void initChannels(){
        Set<String> keySet = redisManager.getKeysPrefix("settings:server:chat:channels:*");

        for (String key : keySet) {
            String channelJson = redisManager.getStringRedis(key);
            ChatChannel channel = redisManager.gson.fromJson(channelJson, ChatChannel.class);
            chatChannels.add(channel);
            channelPlayers.put(channel, new ArrayList<>());
            HighgeekSync.getInstance().logger.warning("Channel loaded: " + channel.name);
        }

    }


    public ChatChannel getChatChannelFromName(String name){
        ChatChannel channel = chatChannels.stream()
                .filter(inv -> inv.name.equals(name))
                .findAny()
                .orElse(null);

        return channel;
    }

    public List<ChatChannel> getDefaultChatChannels(){
        List<ChatChannel> channels = chatChannels.stream()
                .filter(channel -> channel.isDefault())
                .collect(Collectors.toList());
        return channels;
    }
}
