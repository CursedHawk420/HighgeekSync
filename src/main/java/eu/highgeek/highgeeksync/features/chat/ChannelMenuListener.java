package eu.highgeek.highgeeksync.features.chat;

import eu.highgeek.highgeeksync.models.ChatChannel;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ChannelMenuListener implements Listener {

    private ChannelManager channelManager;
    public ChannelMenuListener(ChannelManager channelManager){
        this.channelManager = channelManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getInventory().getHolder() instanceof ChannelMenu menu){
            event.setCancelled(true);
            HighgeekPlayer highgeekPlayer = menu.getHighgeekPlayer();
            ItemStack clickedChannel = event.getCurrentItem();
            if(clickedChannel != null){
                ChatChannel channel = channelManager.getChatChannelFromName(clickedChannel.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("channel"), PersistentDataType.STRING));
                if(event.isRightClick()){
                    highgeekPlayer.setChannelOut(channel);
                    return;
                }
                if(event.isLeftClick()){
                    if(clickedChannel.getType() == Material.RED_WOOL){
                        highgeekPlayer.joinToChannel(channel);
                        //menu.getInventory().setItem(event.getSlot(), greenChannelItem(channel));
                        return;
                    }
                    if(clickedChannel.getType() == Material.GREEN_WOOL){
                        highgeekPlayer.leaveChannel(channel);
                        //menu.getInventory().setItem(event.getSlot(), redChannelItem(channel));
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event){
        if(event.getInventory().getHolder() instanceof ChannelMenu menu){
            HighgeekPlayer highgeekPlayer = menu.getHighgeekPlayer();
            if(event.getPlayer().getName().equals(highgeekPlayer.getPlayer().getName())){
                channelManager.getOpenedChannelMenus().remove(highgeekPlayer.getPlayer().getName());
            }
        }
    }
}
