package eu.highgeek.highgeeksync.features.chat;

import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.models.ChatChannel;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Objects;

public class ChannelMenu implements InventoryHolder {

    private final Inventory inventory;

    @Getter
    private final HighgeekPlayer highgeekPlayer;

    private final ChannelManager channelManager;

    public ChannelMenu(HighgeekPlayer player){
        this.channelManager = HighgeekSync.getChannelManager();
        this.highgeekPlayer = player;
        this.inventory = Bukkit.createInventory(this, 9, "Channel Selector");

        channelManager.getOpenedChannelMenus().put(player.getPlayer().getName(), this);
        init();
        player.getPlayer().openInventory(inventory);
    }

    public void init(){
        inventory.clear();
        inventory.addItem(infoItem());
        inventory.setItem(inventory.getSize() - 1, discordChannelItem());
        setItemChannels();
    }

    private void setItemChannels(){
        for (ChatChannel chatChannel : channelManager.chatChannels){
            if(chatChannel.permission == null){
                inventory.addItem(generateItemStack(chatChannel));
            }else {
                if(highgeekPlayer.getPlayer().hasPermission(chatChannel.permission)){
                    inventory.addItem(generateItemStack(chatChannel));
                }
            }
        }
    }

    public ItemStack generateItemStack(ChatChannel chatChannel){
        if(Objects.equals(highgeekPlayer.getChannelOut(), chatChannel)){
            return blueChannelItem(chatChannel);
        }else
        if(highgeekPlayer.getPlayerChannels().contains(chatChannel)){
            return greenChannelItem(chatChannel);
        }else {
            return redChannelItem(chatChannel);
        }
    }

    public ItemStack redChannelItem(ChatChannel channel){
        ItemStack itemStack = new ItemStack(Material.RED_WOOL);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(channel.getFancyName());
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("channel"), PersistentDataType.STRING, channel.getName());

        meta.setLore(Arrays.asList("Click to join channel", channel.getFancyName()));
        /*
        if(channel.isCanSpeak()){
            meta.setLore(Arrays.asList("Click to join channel", channel.getFancyName()));
        }else {
            meta.setLore(Arrays.asList("Click to join channel", channel.getFancyName(), "You can't speak in this channel."));
        }
        */
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack greenChannelItem(ChatChannel channel){
        ItemStack itemStack = new ItemStack(Material.GREEN_WOOL);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(channel.getFancyName());
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("channel"), PersistentDataType.STRING, channel.getName());

        meta.setLore(Arrays.asList("Currently listening in", channel.getFancyName()));
        /*
        if(channel.isCanSpeak()){
            meta.setLore(Arrays.asList("Currently listening in", channel.getFancyName()));
        }else {
            meta.setLore(Arrays.asList("Currently listening in", channel.getFancyName(), "You can't speak in this channel."));
        }
        */
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack blueChannelItem(ChatChannel channel){
        ItemStack itemStack = new ItemStack(Material.BLUE_WOOL);

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(channel.getFancyName());
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("channel"), PersistentDataType.STRING, channel.getName());

        meta.setLore(Arrays.asList("Currently talking in", channel.getFancyName()));
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack discordChannelItem(){

        if(highgeekPlayer.getPlayerSettingsFromRedis().hasConnectedDiscord){
            ItemStack itemStack = new ItemStack(Material.BLUE_WOOL);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName("Discord");
            meta.setLore(Arrays.asList("Integration is connected"));
            itemStack.setItemMeta(meta);
            return itemStack;
        }else{
            ItemStack itemStack = new ItemStack(Material.RED_WOOL);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName("Discord");
            meta.setLore(Arrays.asList("Integration is disconnected.", "To link your game account with Discord", "send Direct Message to our Discord bot ", "with code: "+ this.highgeekPlayer.getDiscordLinkingCode().getCode()));
            itemStack.setItemMeta(meta);
            return itemStack;
        }
    }

    public ItemStack infoItem(){
        ItemStack itemStack = new ItemStack(Material.WHITE_WOOL);

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("Usage?");

        meta.setLore(Arrays.asList("BLUE - channel you are talking in",
                "GREEN - channel you are listening to",
                "RED - channel you are not connected to",
                "LEFT click - listen/leave channel",
                "RIGHT click - set talking channel"));
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
