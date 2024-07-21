package eu.highgeek.highgeeksync.sync.chat;

import java.util.ArrayList;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.objects.ChatChannel;

public class ChatInitializer {
    
    private static final Gson gson = new GsonBuilder().create();

    public static void channelInitializer(){
        Set<String> keySet = RedisManager.getKeysPrefix("settings:server:chat:channels:*");
        Main.logger.warning("keySet: " + keySet);
        for (String key : keySet) {
            Main.logger.warning("Loading channel: " + key);
            String channelJson = RedisManager.getStringRedis(key);
            Main.logger.warning("channelJson: " + channelJson);
            ChatChannel channel = gson.fromJson(channelJson, ChatChannel.class);
            ChannelManager.chatChannels.add(channel);
            ChannelManager.channelPlayers.put(channel, new ArrayList<>());
        }
        Main.logger.warning("Registered channels: ");
        for (ChatChannel channel :  ChannelManager.chatChannels) {
            Main.logger.warning("Name: " + channel.name + ", Prefix: " + channel.prefix + ", isDefault: " + channel.isDefault + ", isLocal: " + channel.isLocal);
        }
    }
}
