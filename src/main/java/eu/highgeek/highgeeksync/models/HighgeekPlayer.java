package eu.highgeek.highgeeksync.models;

import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.data.sql.entities.VirtualInventories;
import eu.highgeek.highgeeksync.features.chat.ChannelManager;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HighgeekPlayer {

    @Getter
    private final Player player;

    private final List<VirtualInventories> virtualInventories;

    private final ChannelManager channelManager;
    private final RedisManager redisManager;

    private final PlayerSettings playerSettings;
    private final List<ChatChannel> playerChannels = new ArrayList<>();
    @Getter
    private ChatChannel channelOut;

    public HighgeekPlayer(Player player, RedisManager redisManager, ChannelManager channelManager){
        HighgeekSync.getInstance().logger.warning("Player joined!");
        this.player = player;
        this.channelManager = channelManager;
        this.redisManager = redisManager;

        this.virtualInventories = getPlayerInventories();
        this.playerSettings = getPlayerSettings();

        for (String stringChannel : playerSettings.joinedChannels) {
            ChatChannel playChannel = channelManager.chatChannels.stream()
                    .filter(s -> s.name.equals(stringChannel))
                    .findAny()
                    .orElse(null);
            playerChannels.add(playChannel);

            channelManager.channelPlayers.get(playChannel).addLast(this);

            HighgeekSync.getInstance().logger.warning("Channel in joined: " + playChannel.name);
        }
        channelOut = channelManager.chatChannels.stream()
                .filter(s -> s.name.equals(playerSettings.channelOut))
                .findAny()
                .orElse(null);
        HighgeekSync.getInstance().logger.warning("Channel out joined: " + channelOut.name);
        HighgeekSync.getInstance().getHighgeekPlayers().put(player.getUniqueId(), this);
    }

    public void joinToChannel(ChatChannel channel){
        if(!this.playerChannels.contains(channel)){
            //Add to this joined channels
            this.playerChannels.add(channel);
            //Add to global channel list
            channelManager.channelPlayers.get(channel).add(this);
        }
    }

    public void leaveChannel(ChatChannel channel){
        if(this.playerChannels.contains(channel)){
            //Remove from this channel list
            this.playerChannels.remove(channel);
            //Remove from global channel list
            channelManager.channelPlayers.get(channel).remove(this);
        }
    }

    public void setChannelOut(ChatChannel channel){
        if(this.channelOut != channel){
            //Set this channel out
            this.channelOut = channel;
            joinToChannel(channel);
        }
    }


    private List<VirtualInventories> getPlayerInventories(){
        List<VirtualInventories> inventories = HighgeekSync.getVirtualInventoryController().getPlayerVirtualInventories(player);
        if(inventories.isEmpty()){
            //Assume first time player
            inventories.add(HighgeekSync.getVirtualInventoryController().createNewVirtualInventory(player, "default", 27, false));
            inventories.add(HighgeekSync.getVirtualInventoryController().createNewVirtualInventory(player, "default", 27, true));
        }
        return inventories;
    }



    public void setPlayerSettings(PlayerSettings playerSettings){
        redisManager.setStringRedis("players:settings:"+playerSettings.playerName, redisManager.gson.toJson(playerSettings));
    }

    public PlayerSettings getPlayerSettings(){
        String playerSettings = redisManager.getStringRedis("players:settings:"+player.getName());
        if (playerSettings == null){
            PlayerSettings newPlayerSettings = new PlayerSettings(player.getName(), player.getUniqueId().toString(), HighgeekSync.getChannelManager().getDefaultChatChannels().stream().map(ChatChannel::getName).collect(Collectors.toList()), "global", false, new ArrayList<>());
            setPlayerSettings(newPlayerSettings);
            return newPlayerSettings;
        }else{
            return redisManager.gson.fromJson(redisManager.getStringRedis("players:settings:"+player.getName()), PlayerSettings.class);
        }
    }



    public void onDisconnectAsync(){
        //channelManager.channelPlayers.remove(this);
    }
}
