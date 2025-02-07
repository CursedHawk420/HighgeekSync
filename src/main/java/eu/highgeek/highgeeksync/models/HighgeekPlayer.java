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
    @Getter
    private List<ChatChannel> playerChannels = new ArrayList<>();
    @Getter
    private ChatChannel channelOut;

    public HighgeekPlayer(Player player, RedisManager redisManager, ChannelManager channelManager){
        HighgeekSync.getInstance().logger.warning("Player joined!");
        this.player = player;
        this.channelManager = channelManager;
        this.redisManager = redisManager;

        this.virtualInventories = getPlayerInventories();
        this.playerSettings = getPlayerSettings();
        this.playerSettings.player = this;

        initChannels();
        HighgeekSync.getInstance().getHighgeekPlayers().put(player.getName(), this);
    }

    public void initChannels(){
        this.playerChannels = playerSettings.getJoinedChannels();
        this.channelOut = playerSettings.getChannelOut();
    }

    public void joinToChannel(ChatChannel channel){
        if(!this.playerChannels.contains(channel)){
            this.playerSettings.joinedChannels.add(channel.name);
            redisManager.setPlayerSettings(this.playerSettings);
        }
    }

    public void leaveChannel(ChatChannel channel){
        if(this.playerChannels.contains(channel)){
            this.playerSettings.joinedChannels.remove(channel.name);
            redisManager.setPlayerSettings(this.playerSettings);
        }
    }

    public boolean setChannelOut(ChatChannel channel){
        if(channel.speakPermission == null){
            if(this.channelOut != channel)
            {
                this.playerSettings.channelOut = channel.name;
                redisManager.setPlayerSettings(this.playerSettings);
                joinToChannel(channel);
                return true;
            }
            return false;
        }
        else {
            if(player.hasPermission(channel.speakPermission)){
                //Set this channel out
                this.playerSettings.channelOut = channel.name;
                redisManager.setPlayerSettings(this.playerSettings);
                joinToChannel(channel);
                return true;
            }else {
                return false;
            }
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
