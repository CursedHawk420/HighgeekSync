package eu.highgeek.highgeeksync.features.chat;

import eu.highgeek.highgeeksync.listeners.ChatListener;
import eu.highgeek.highgeeksync.models.ChatChannel;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class ChannelMenu implements InventoryHolder, Listener {

    private final Inventory inventory;

    private final HighgeekPlayer highgeekPlayer;

    public ChannelMenu(HighgeekPlayer player){
        this.highgeekPlayer = player;
        this.inventory = Bukkit.createInventory(this, 9, "Channel Selector");
    }

    public ItemStack redChannelItem(ChatChannel channel){
        ItemStack itemStack = new ItemStack(Material.RED_WOOL);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(channel.getFancyName());
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("channel"), PersistentDataType.STRING, channel.getName());
        if(channel.isCanSpeak()){
            meta.setLore(Arrays.asList("Click to join channel", channel.getFancyName()));
        }else {
            meta.setLore(Arrays.asList("Click to join channel", channel.getFancyName(), "You can't speak in this channel."));
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack greenChannelItem(ChatChannel channel){
        ItemStack itemStack = new ItemStack(Material.GREEN_WOOL);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(channel.getFancyName());
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("channel"), PersistentDataType.STRING, channel.getName());
        if(channel.isCanSpeak()){
            meta.setLore(Arrays.asList("Currently listening in", channel.getFancyName()));
        }else {
            meta.setLore(Arrays.asList("Currently listening in", channel.getFancyName(), "You can't speak in this channel."));
        }
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

        if(highgeekPlayer.getPlayerSettings().hasConnectedDiscord){
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
            //meta.setLore(Arrays.asList("Integration is disconnected.", "To link your game account with Discord", "send Direct Message to our Discord bot ", "with code: "+ DiscordUtil.codeMap.get(player)));
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getInventory().getHolder() instanceof ChannelMenu){

        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
