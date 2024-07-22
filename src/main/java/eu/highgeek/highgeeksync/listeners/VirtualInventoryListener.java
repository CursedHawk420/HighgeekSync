package eu.highgeek.highgeeksync.listeners;

import static eu.highgeek.highgeeksync.Main.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import eu.highgeek.highgeeksync.menus.ChannelSelector;
import eu.highgeek.highgeeksync.objects.ChatChannel;
import eu.highgeek.highgeeksync.sync.chat.ChannelManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.events.RedisInventorySetEvent;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.events.RedisNewInventoryEvent;
import eu.highgeek.highgeeksync.data.sql.MysqlVirtualInventoryManager;
import eu.highgeek.highgeeksync.sync.inventory.InventoryManager;
import eu.highgeek.highgeeksync.sync.inventory.VirtualInventoryHolder;

public class VirtualInventoryListener implements Listener {

    //item extracted event
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked(); // The player that clicked the item
        if (event.getClickedInventory() != null && event.getCurrentItem() != null){
            if((player.getOpenInventory().getTopInventory().getHolder() instanceof ChannelSelector )){

                if(event.getClickedInventory().getHolder() instanceof ChannelSelector channelSelector)
                {
                    ItemStack itemStack = event.getCurrentItem();

                    if(event.getClick() == ClickType.RIGHT){
                        ItemStack item = event.getClickedInventory().getItem(event.getClickedInventory().first(Material.BLUE_WOOL));
                        item = channelSelector.leaveChannelItem(ChannelManager.getChatChannelFromName(item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("channel"), PersistentDataType.STRING)));
                        event.getClickedInventory().setItem(event.getClickedInventory().first(Material.BLUE_WOOL), item);

                        ChatChannel channel = ChannelManager.getChatChannelFromName(itemStack.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("channel"), PersistentDataType.STRING));

                        channelSelector.getInventory().setItem(event.getSlot(), channelSelector.blueChannelItem(channel));
                        channelSelector.initializeItems();

                        ChannelManager.setChannelOut(ChannelManager.getChatPlayer(player), channel);
                        channelSelector.updateInv();

                    }
                    else if (event.getClick() == ClickType.LEFT){
                        if(itemStack.getType() == Material.RED_WOOL){
                            ChatChannel channel = ChannelManager.getChatChannelFromName(itemStack.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("channel"), PersistentDataType.STRING));
                            channelSelector.getInventory().setItem(event.getSlot(), channelSelector.leaveChannelItem(channel));
                            ChannelManager.joinPlayerToChannel(ChannelManager.getChatPlayer(player), channel);

                        }
                        if(itemStack.getType() == Material.GREEN_WOOL){
                            ChatChannel channel = ChannelManager.getChatChannelFromName(itemStack.getItemMeta().getPersistentDataContainer().get(NamespacedKey.fromString("channel"), PersistentDataType.STRING));
                            channelSelector.getInventory().setItem(event.getSlot(), channelSelector.joinChannelItem(channel));
                            ChannelManager.disconnectPlayerFromChannel(ChannelManager.getChatPlayer(player), channel);

                        }
                    }

                }
                event.setCancelled(true);
                return;
            }
            if((player.getOpenInventory().getTopInventory().getHolder() instanceof VirtualInventoryHolder virtualInventoryHolder))
            {
                if (event.getCurrentItem().getType() == Material.BARRIER){
                    event.setCancelled(true);
                    //player.sendMessage("");
                    return;
                }
                else
                {

//                    Inventory inventory = event.getInventory(); // The inventory that was clicked in
//                    ItemStack clicked = event.getCurrentItem(); // The item that was clicked
                    String invUuid = virtualInventoryHolder.getInventoryUuid(); //uuid of VirtualInventory
                    String ownerName = virtualInventoryHolder.getVirtualInventoryUuid().getPlayerName();
                    InventoryAction action = event.getAction();
                    int i = event.getSlot();
//                    int b = event.getRawSlot();

                    Inventory vinv = player.getOpenInventory().getTopInventory();
                    Inventory pinv = player.getOpenInventory().getBottomInventory();
                    final ItemStack[] oldvinv = vinv.getContents();
//                    event.getClick();
//                    event.isLeftClick();
//                    event.isRightClick();
//                    event.isShiftClick();
//                    event.getCurrentItem();
//                    event.getSlot();

                    String itemUuid = "vinv:"+ownerName+":"+invUuid+":";

                    if(event.getClickedInventory().getHolder() instanceof VirtualInventoryHolder)
                    {
                        int b = indexOf(pinv.getContents(), event.getCurrentItem());
                        Main.logger.info("//kliknuto do vinvu " + event.getSlot());
                        if (action != InventoryAction.NOTHING && action == InventoryAction.MOVE_TO_OTHER_INVENTORY){
                            RedisManager.setItemInRedis(itemUuid + event.getSlot(),new ItemStack(Material.AIR));
                        }
                        else {
                            event.setCancelled(true);
                        }
                    }
                    else
                    {
                        int b = indexOf(vinv.getContents(), event.getCurrentItem());
                        Main.logger.info("//kiknuto do playerinvu při otevřenem vinvu " + event.getSlot());
                        if (action != InventoryAction.NOTHING && action == InventoryAction.MOVE_TO_OTHER_INVENTORY){
                            vinv.getContents();
                            Main.logger.info("//indexOf: "+b);
                            //ManageRedisData.setInventoryItem(invUuid,vinv.firstEmpty(),event.getCurrentItem());
                            BukkitScheduler scheduler = main.getServer().getScheduler();
                            scheduler.scheduleSyncDelayedTask(main, new Runnable() {
                                public void run() {
                                    ItemStack[] newvinv = player.getOpenInventory().getTopInventory().getContents();
                                    Material oldType;
                                    Material newType;
                                    int oldAmount;
                                    int newAmount;
                                    for (int i = 0; i < newvinv.length; i++){
                                        if (oldvinv[i] != newvinv[i]){
                                            if (newvinv[i].getType() != Material.BARRIER){
                                                RedisManager.setItemInRedis(itemUuid + i,newvinv[i]);
                                            }
                                        }
                                    }
                                }
                            },2);
                        }
                        else {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();
        if((player.getOpenInventory().getTopInventory().getHolder() instanceof VirtualInventoryHolder virtualInventoryHolder))
        {
            List<UUID> players = InventoryManager.openedInventories.get(virtualInventoryHolder.getInventoryUuid());
            players.remove(player.getUniqueId());
            if (players.isEmpty()){
                InventoryManager.openedInventories.remove(virtualInventoryHolder.getInventoryUuid());
            }else {
                InventoryManager.openedInventories.put(virtualInventoryHolder.getInventoryUuid(), players);
            }
        }
    }

    public static <T> int indexOf(T[] arr, T val) {
        return Arrays.asList(arr).indexOf(val);
    }


    @EventHandler
    public void onRedisInventorySetEvent(RedisInventorySetEvent event){
        Main.logger.warning("Custom event fired!");

        List<UUID> uuids = InventoryManager.openedInventories.get(event.getInvUuid());
        for (var item : uuids
        ) {
            Player player = Bukkit.getServer().getPlayer(item);
            Main.logger.warning("Playername with open inventory: " + player.getName());

            ItemStack newItem = RedisManager.getItemFromRedis(event.getRawMessage());

            player.getOpenInventory().getTopInventory().setItem(event.getSlotId(), newItem);
        }
    }
    
    @EventHandler
    public void onRedisNewInventoryEvent(RedisNewInventoryEvent event){
        Main.logger.warning("onRedisNewInventoryEvent fired!");
        MysqlVirtualInventoryManager.loadNewInventory(event.getInvUuid());
    }
}
