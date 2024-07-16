package eu.highgeek.highgeeksync.websync.listeners;

import static eu.highgeek.highgeeksync.Main.*;
import static eu.highgeek.highgeeksync.websync.DataManager.*;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.MainManageData;
import eu.highgeek.highgeeksync.websync.data.ManageRedisData;
import eu.highgeek.highgeeksync.websync.inventory.VirtualInventoryHolder;

public class VirtualInventoryListener implements Listener {
    //item extracted event
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked(); // The player that clicked the item
        if (event.getClickedInventory() != null && event.getCurrentItem() != null){
            if((player.getOpenInventory().getTopInventory().getHolder() instanceof VirtualInventoryHolder virtualInventoryHolder))
            {
                if (event.getCurrentItem().getType() == Material.BARRIER){
                    event.setCancelled(true);
                    //player.sendMessage("");
                    return;
                }
                else
                {

                    Inventory inventory = event.getInventory(); // The inventory that was clicked in
                    ItemStack clicked = event.getCurrentItem(); // The item that was clicked
                    String invUuid = virtualInventoryHolder.getInventoryUuid(); //uuid of VirtualInventory
                    String playername = virtualInventoryHolder.getVirtualInventoryUuid().getPlayerName();
                    InventoryAction action = event.getAction();
                    int i = event.getSlot();
                    //int b = event.getRawSlot();

                    Inventory vinv = player.getOpenInventory().getTopInventory();
                    Inventory pinv = player.getOpenInventory().getBottomInventory();
                    final ItemStack[] oldvinv = vinv.getContents();
                    event.getClick();
                    event.isLeftClick();
                    event.isRightClick();
                    event.isShiftClick();
                    event.getCurrentItem();
                    event.getSlot();

                    if(event.getClickedInventory().getHolder() instanceof VirtualInventoryHolder)
                    {
                        int b = indexOf(pinv.getContents(), event.getCurrentItem());
                        Main.logger.info("//kliknuto do vinvu " + event.getSlot());
//                switch (action){
//                    case NOTHING : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.NOTHING");
//                    }
//                    case PICKUP_ALL : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.PICKUP_ALL");
//                    }
//                    case PICKUP_SOME : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.PICKUP_SOME");
//                    }
//                    case PICKUP_HALF : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.PICKUP_HALF");
//                    }
//                    case PICKUP_ONE : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.PICKUP_ONE");
//                    }
//                    case PLACE_ALL : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.PLACE_ALL");
//                    }
//                    case PLACE_SOME : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.PLACE_SOME");
//                    }
//                    case PLACE_ONE : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.PLACE_ONE");
//                    }
//                    case SWAP_WITH_CURSOR : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.SWAP_WITH_CURSOR");
//                    }
//                    case DROP_ALL_CURSOR : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.DROP_ALL_CURSOR");
//                    }
//                    case DROP_ONE_CURSOR : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.DROP_ONE_CURSOR");
//                    }
//                    case DROP_ALL_SLOT : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.DROP_ALL_SLOT");
//                    }
//                    case DROP_ONE_SLOT : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.DROP_ONE_SLOT");
//                    }
//                    case MOVE_TO_OTHER_INVENTORY : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.MOVE_TO_OTHER_INVENTORY");
//                    }
//                    case HOTBAR_MOVE_AND_READD : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.HOTBAR_MOVE_AND_READD");
//                    }
//                    case HOTBAR_SWAP : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.HOTBAR_SWAP");
//                    }
//                    case CLONE_STACK : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.CLONE_STACK");
//                    }
//                    case COLLECT_TO_CURSOR : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.COLLECT_TO_CURSOR");
//                    }
//                    case UNKNOWN : {
//                        Main.logger.info("//vinvu - i="+i+" b="+b+" InventoryAction.UNKNOWN");
//                    }
//                }
                        if (action != InventoryAction.NOTHING && action == InventoryAction.MOVE_TO_OTHER_INVENTORY){
                            ManageRedisData.setInventoryItem(invUuid,event.getSlot(),new ItemStack(Material.AIR));
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
                                                ManageRedisData.setInventoryItem(invUuid,i,newvinv[i]);
                                            }
                                        }
                                    }
                                }
                            },2);
                        }
                        else {
                            event.setCancelled(true);
                        }
//                switch (action){
//                    case NOTHING : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.NOTHING");
//                    }
//                    case PICKUP_ALL : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.PICKUP_ALL");
//                    }
//                    case PICKUP_SOME : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.PICKUP_SOME");
//                    }
//                    case PICKUP_HALF : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.PICKUP_HALF");
//                    }
//                    case PICKUP_ONE : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.PICKUP_ONE");
//                    }
//                    case PLACE_ALL : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.PLACE_ALL");
//                    }
//                    case PLACE_SOME : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.PLACE_SOME");
//                    }
//                    case PLACE_ONE : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.PLACE_ONE");
//                    }
//                    case SWAP_WITH_CURSOR : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.SWAP_WITH_CURSOR");
//                    }
//                    case DROP_ALL_CURSOR : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.DROP_ALL_CURSOR");
//                    }
//                    case DROP_ONE_CURSOR : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.DROP_ONE_CURSOR");
//                    }
//                    case DROP_ALL_SLOT : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.DROP_ALL_SLOT");
//                    }
//                    case DROP_ONE_SLOT : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.DROP_ONE_SLOT");
//                    }
//                    case MOVE_TO_OTHER_INVENTORY : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.MOVE_TO_OTHER_INVENTORY");
//                    }
//                    case HOTBAR_MOVE_AND_READD : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.HOTBAR_MOVE_AND_READD");
//                    }
//                    case HOTBAR_SWAP : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.HOTBAR_SWAP");
//                    }
//                    case CLONE_STACK : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.CLONE_STACK");
//                    }
//                    case COLLECT_TO_CURSOR : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.COLLECT_TO_CURSOR");
//                    }
//                    case UNKNOWN : {
//                        Main.logger.info("//playerinvu - i="+i+" b="+b+" InventoryAction.UNKNOWN");
//                    }
//                }
                    }
                    /*

                    PICKUP_ALL,
                    PICKUP_SOME,
                    PICKUP_HALF,
                    PICKUP_ONE,
                    PLACE_ALL,
                    PLACE_SOME,
                    PLACE_ONE,
                    SWAP_WITH_CURSOR,
                    DROP_ALL_CURSOR,
                    DROP_ONE_CURSOR,
                    DROP_ALL_SLOT,
                    DROP_ONE_SLOT,
                    MOVE_TO_OTHER_INVENTORY,
                    HOTBAR_MOVE_AND_READD,
                    HOTBAR_SWAP,
                    CLONE_STACK,
                    COLLECT_TO_CURSOR,
                    UNKNOWN;

                    */
                }}
        }
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();
        if((player.getOpenInventory().getTopInventory().getHolder() instanceof VirtualInventoryHolder virtualInventoryHolder))
        {
            ArrayList<UUID> players = MainManageData.openedInventories.get(virtualInventoryHolder.getInventoryUuid());
            players.remove(player.getUniqueId());
            if (players.isEmpty()){
                MainManageData.openedInventories.remove(virtualInventoryHolder.getInventoryUuid());
            }else {
                MainManageData.openedInventories.put(virtualInventoryHolder.getInventoryUuid(), players);
            }
        }
    }
}
