package eu.highgeek.highgeeksync.websync.inventory;


import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.MainManageData;
import eu.highgeek.highgeeksync.websync.DataManager;
import eu.highgeek.highgeeksync.websync.data.ManageRedisData;
import eu.highgeek.highgeeksync.websync.objects.VirtualInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

//https://github.com/kroimon/Bukkit-AlphaChest/blob/master/src/main/java/net/sradonia/bukkit/alphachest

public class VirtualInventoryManager {

//    public static ItemStack[] getPlayerInventory(Player player){
//        ItemStack[] items = new ItemStack[player.getInventory().getSize()];
//        int i = 0;
//        PlayerInventory inventory = player.getInventory();
//        for (ItemStack item: inventory
//        ) {
//            items[i] = inventory.getItem(i);
//            i++;
//        }
//        return items;
//    }

    public void openInventory(Player player, String uuid) {


        Main.logger.info("playersDefaultInventories number: " + String.valueOf(MainManageData.playersDefaultInventories.size()));
        if (uuid == "default"){
            uuid = MainManageData.playersDefaultInventories.get(player.getUniqueId()).getInvUuid();
        }


        VirtualInventory vinv = MainManageData.inventoriesObjects.get(uuid);

        Inventory inv = Bukkit.createInventory(new VirtualInventoryHolder(uuid, vinv), vinv.getSize(), "§3Web Chest " + vinv.getInvName());
        for (int i = 0; i < vinv.getSize() ; i++) {
            inv.setItem(i, ManageRedisData.getInventoryItem(vinv, i));
        }
        ArrayList<UUID> uuids = MainManageData.openedInventories.get(uuid);
        if (uuids == null){
            uuids = new ArrayList<>();
        }
        uuids.add(player.getUniqueId());
        MainManageData.openedInventories.put( uuid,uuids);
        player.openInventory(inv);

    }



    public void createInventory(Player player, String name, int size){
        String uuid = UUID.randomUUID().toString();
        VirtualInventory vinv = new VirtualInventory();
        vinv.setIsWeb(false);
        vinv.setSize(size);
        vinv.setOwnerUuid(player.getUniqueId().toString());
        vinv.setPlayerName(player.getName());
        vinv.setInvUuid(uuid);
        vinv.setInvName(name);
        MainManageData.inventoriesObjects.put(uuid, vinv);
        ArrayList<String> uuids = MainManageData.playerInventoriesHashMap.get(player.getUniqueId());
        uuids.add(uuid);
        MainManageData.playerInventoriesHashMap.put(player.getUniqueId(),uuids);
        MainManageData.inventoriesIds.put(uuid, player.getName());

        for (int i = 0 ; i < size; i++){
            ManageRedisData.setInventoryItem(uuid,i, new ItemStack(Material.AIR));
        }
        DataManager.savePlayerInventoryToDatabase(vinv, player);

        openInventory(player, uuid);
    }

    public void createInventory(Player player, String name, int size, String type){
        boolean web = false;
        if (Objects.equals(type, "web")){
            web = true;
        }
        String uuid = UUID.randomUUID().toString();
        VirtualInventory vinv = new VirtualInventory();
        vinv.setIsWeb(web);
        vinv.setSize(size);
        vinv.setOwnerUuid(player.getUniqueId().toString());
        vinv.setPlayerName(player.getName());
        vinv.setInvUuid(uuid);
        vinv.setInvName(name);
        MainManageData.inventoriesObjects.put(uuid, vinv);
        ArrayList<String> uuids = MainManageData.playerInventoriesHashMap.get(player.getUniqueId());
        uuids.add(uuid);
        MainManageData.playerInventoriesHashMap.put(player.getUniqueId(),uuids);
        MainManageData.inventoriesIds.put(uuid, player.getName());

        for (int i = 0 ; i < size; i++){
            ManageRedisData.setDefaultInventoryItem(uuid,i,vinv ,new ItemStack(Material.AIR));
        }
        DataManager.savePlayerInventoryToDatabase(vinv, player);

        openInventory(player, uuid);
    }

    /*public void openInventory(Player player, String uuid) {
        TestMenu menu = new TestMenu();
        menu.uuid = uuid;
        Main.odalitaMenus.openMenu(menu, player);
    }*/
}
