package eu.highgeek.highgeeksync.sync.chat;

import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.objects.ChatChannel;

public class ChatInitializer {
    
    private static final Gson gson = new GsonBuilder().create();

    public static void channelInitializer(){
        Set<String> keySet = RedisManager.getKeysPrefix("settings:server:chatchannels");
        for (String key : keySet) {
            ChatChannel channel = gson.fromJson(RedisManager.getRedis(key), ChatChannel.class);
            ChannelManager.chatChannels.add(channel);
            if (channel.isDefault){
                ChannelManager.defaultChannels.add(channel.name);
            }
        }
    }
}
