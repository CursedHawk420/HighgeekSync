package eu.highgeek.highgeeksync.models;

import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.data.sql.entities.DiscordLinkingCode;
import eu.highgeek.highgeeksync.data.sql.entities.VirtualInventories;
import eu.highgeek.highgeeksync.features.adapters.ItemStackAdapter;
import eu.highgeek.highgeeksync.features.chat.ChannelManager;
import eu.highgeek.highgeeksync.features.virtualinventories.VirtualInventoryHolder;
import eu.highgeek.highgeeksync.utils.DiscordLinkingUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HighgeekPlayer {

    @Getter
    private final Player player;

    private final List<VirtualInventories> virtualInventories;

    @Getter
    @Nullable
    private DiscordLinkingCode discordLinkingCode;

    private final ChannelManager channelManager;
    private final RedisManager redisManager;

    @Setter
    @Getter
    private PlayerSettings playerSettings;
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
        this.playerSettings = getPlayerSettingsFromRedis();
        this.playerSettings.player = this;
        initChannels();
        if(!this.playerSettings.hasConnectedDiscord){
            this.discordLinkingCode = HighgeekSync.getDiscordLinkingCodeController().getPlayerDiscordLinkingCode(this.player);
        }
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
            channelManager.channelPlayers.get(channel).remove(this);
            redisManager.setPlayerSettings(this.playerSettings);
        }
    }

    public boolean setChannelOut(ChatChannel channel){
        if(channel.speakPermission == null){
            if(this.channelOut != channel)
            {
                this.playerSettings.channelOut = channel.name;
                if(!this.playerChannels.contains(channel)){
                    this.playerSettings.joinedChannels.add(channel.name);
                }
                redisManager.setPlayerSettings(this.playerSettings);
                return true;
            }
            return false;
        }
        else {
            if(player.hasPermission(channel.speakPermission)){
                //Set this channel out
                this.playerSettings.channelOut = channel.name;
                if(!this.playerChannels.contains(channel)){
                    this.playerSettings.joinedChannels.add(channel.name);
                }
                redisManager.setPlayerSettings(this.playerSettings);
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
            fistTimeLogin();
        }
        return inventories;
    }

    public void openDefaultInventory(){
        for (VirtualInventories vinv : virtualInventories){
            if(Objects.equals(vinv.getInventoryName(), "default") && !vinv.getWeb()){
                HighgeekSync.getInventoriesManager().openSpecificVirtualInventory(vinv, player);
            }
        }
    }

    private void fistTimeLogin(){
        HighgeekSync.getDiscordLinkingCodeController().createNewDiscordLinkingCode(this.player, DiscordLinkingUtils.generateLinkingCode());
    }

    public void setPlayerSettingsInRedis(PlayerSettings playerSettings){
        redisManager.setStringRedis("players:settings:"+playerSettings.playerName, redisManager.gson.toJson(playerSettings));
    }

    public PlayerSettings getPlayerSettingsFromRedis(){
        String playerSettings = redisManager.getStringRedis("players:settings:"+player.getName());
        if (playerSettings == null){
            PlayerSettings newPlayerSettings = new PlayerSettings(player.getName(), player.getUniqueId().toString(), HighgeekSync.getChannelManager().getDefaultChatChannels().stream().map(ChatChannel::getName).collect(Collectors.toList()), "global", false, new ArrayList<>());
            setPlayerSettingsInRedis(newPlayerSettings);
            return newPlayerSettings;
        }else{
            return redisManager.gson.fromJson(redisManager.getStringRedis("players:settings:"+player.getName()), PlayerSettings.class);
        }
    }


    public void onDisconnectAsync(){
        HighgeekSync.getInstance().getHighgeekPlayers().remove(player.getName());
        for(ChatChannel channel : playerChannels){
            channelManager.channelPlayers.get(channel).remove(this);
        }
    }
}
