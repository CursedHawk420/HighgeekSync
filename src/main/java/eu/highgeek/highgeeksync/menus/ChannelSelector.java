package eu.highgeek.highgeeksync.menus;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import eu.highgeek.highgeeksync.common.Common;
import eu.highgeek.highgeeksync.objects.ChatChannel;
import eu.highgeek.highgeeksync.objects.PlayerSettings;
import eu.highgeek.highgeeksync.sync.chat.ChannelManager;
import eu.highgeek.highgeeksync.sync.chat.DiscordUtil;

public final class ChannelSelector implements InventoryHolder
{


    private final Inventory inventory;
    private final Player player;
    private PlayerSettings playerSettings;

    public ChannelSelector(Player player){
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 9, "Channel Selector");

        this.playerSettings = Common.playerSettings.get(player);

        initializeItems();

        player.openInventory(inventory);
    }


    public void initializeItems() {
        inventory.clear();
        inventory.addItem(infoItem());
        for (ChatChannel chatChannel : ChannelManager.chatChannels){
            inventory.addItem(generateItemStack(chatChannel));
        }
        inventory.setItem(inventory.getSize() - 1, discordChannelItem());
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

    public ItemStack generateItemStack(ChatChannel chatChannel){
        if(playerSettings.channelOut.contains(chatChannel.name)){
            return blueChannelItem(chatChannel);
        }else
        if(playerSettings.joinedChannels.contains(chatChannel.name)){
            return leaveChannelItem(chatChannel);
        }else {
            return joinChannelItem(chatChannel);
        }
    }

    public ItemStack leaveChannelItem(ChatChannel channel){
        ItemStack itemStack = new ItemStack(Material.GREEN_WOOL);

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(channel.getFancyName());
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("channel"), PersistentDataType.STRING, channel.getName());

        meta.setLore(Arrays.asList("Currently listening in", channel.getFancyName()));
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack joinChannelItem(ChatChannel channel){
        ItemStack itemStack = new ItemStack(Material.RED_WOOL);

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(channel.getFancyName());
        meta.getPersistentDataContainer().set(NamespacedKey.fromString("channel"), PersistentDataType.STRING, channel.getName());

        meta.setLore(Arrays.asList("Click to join channel", channel.getFancyName()));
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

        if(playerSettings.hasConnectedDiscord){
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
            meta.setLore(Arrays.asList("Integration is disconnected.", "To link your game account with Discord", "send Dicrect Message to our Discord bot ", "with code: "+ DiscordUtil.codeMap.get(player)));
            itemStack.setItemMeta(meta);
            return itemStack;
        }



    }

    public void updateInv(){
        this.playerSettings = Common.playerSettings.get(player);
        initializeItems();
    }


    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
