package eu.highgeek.highgeeksync.websync;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.MainManageData;
import eu.highgeek.highgeeksync.websync.data.ManageMysqlData;
import eu.highgeek.highgeeksync.websync.data.ManageRedisData;
import eu.highgeek.highgeeksync.websync.objects.VirtualInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class DataManager {
    /*
    public static void saveVirtualInventory(Player player, ItemStack[] items){
        UUID uuid = UUID.randomUUID();
        String uuidString = "virtual-"+uuid.toString();
        JsonObject json = new JsonObject();
        json.addProperty("inv-id", uuidString);
        json.addProperty("player-uuid", player.getUniqueId().toString());
        json.addProperty("owner-name", player.getName());
        int i = 1;
        for (ItemStack item : items
        ) {
            json.addProperty(String.valueOf(i), ItemAdapter.toJson(item));
            String itemuuid = uuidString +"-"+ i;
            ManageRedisData.setJson(itemuuid, ItemAdapter.toJson(item));

            i++;
        }
        ManageMysqlData.saveVirtualInventoryInDatabase(player.getUniqueId().toString(), player.getName(),uuidString, json.toString());
    }
    public static void saveVirtualInventory(String uuid, Player player, ItemStack[] items){
        JsonObject json = new JsonObject();
        json.addProperty("inv-id", uuid);
        json.addProperty("player-uuid", player.getUniqueId().toString());
        json.addProperty("owner-name", player.getName());
        int i = 1;
        for (ItemStack item : items
        ) {
            json.addProperty(String.valueOf(i), ItemAdapter.toJson(item));
            String itemuuid = uuid +"-"+ i;
            ManageRedisData.setJson(itemuuid, ItemAdapter.toJson(item));
            i++;
        }
        ManageMysqlData.saveVirtualInventoryInDatabase(player.getUniqueId().toString(), player.getName(),uuid, json.toString());
    }
    public static void saveVirtualInventory(Player player){
        UUID uuid = UUID.randomUUID();
        String uuidString = "player-"+uuid.toString();
        PlayerInventory items = player.getInventory();
        JsonObject json = new JsonObject();
        json.addProperty("inv-id", uuidString);
        json.addProperty("player-uuid", player.getUniqueId().toString());
        json.addProperty("owner-name", player.getName());
        int i = 1;
        for (ItemStack item : items
        ) {
            json.addProperty(String.valueOf(i), ItemAdapter.toJson(item));
            String itemuuid = uuidString +"-"+ i;
            ManageRedisData.setJson(itemuuid, ItemAdapter.toJson(item));
            i++;
        }
        ManageMysqlData.saveVirtualInventoryInDatabase(player.getUniqueId().toString(), player.getName(),uuidString, json.toString());
    }
    public static void saveVirtualInventory(String uuid, Player player){
        PlayerInventory items = player.getInventory();
        JsonObject json = new JsonObject();
        json.addProperty("inv-id", uuid);
        json.addProperty("player-uuid", player.getUniqueId().toString());
        json.addProperty("owner-name", player.getName());
        int i = 1;
        for (ItemStack item : items
        ) {
            json.addProperty(String.valueOf(i), ItemAdapter.toJson(item));
            String itemuuid = uuid +"-"+ i;
            ManageRedisData.setJson(itemuuid, ItemAdapter.toJson(item));
            i++;
        }
        ManageMysqlData.saveVirtualInventoryInDatabase(player.getUniqueId().toString(), player.getName(),uuid, json.toString());
    }

    public static void setFullVirtualInventory(String uuid, Inventory inventory){
        ManageRedisData.getJson(uuid);
        ManageMysqlData.loadVirtualInventory(uuid);
    }

    //on redis item update
    public static void setVirtualItemInInventory(){

    }*/

    public static void createDefaultInventory(Player player){

    }

    //on player join
    public static void loadPlayerInventoriesUuids(Player player){
        try {
            ArrayList<String> uuids = ManageMysqlData.loadPlayerVirtualInventoriesUuids(player);
            if (!uuids.isEmpty()){
                MainManageData.playerInventoriesHashMap.put(player.getUniqueId(), uuids);
                ManageMysqlData.loadPlayerVirtualInventoriesObjects(player);
            }
        }catch (Exception e){
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.main, new Runnable() {
                @Override
                public void run() {
                    loadPlayerInventoriesUuids(player);
                }
            }, 5);
        }
    }

    //on player leave
    public static void savePlayerInventoriesToDatabase(Player player){

        ArrayList<String> uuids = MainManageData.playerInventoriesHashMap.get(player.getUniqueId());
        for (String uuid: uuids
        ) {
            int size = MainManageData.inventoriesObjects.get(uuid).getSize();

            JsonObject items = new JsonObject();
            for (int i = 0; i < size ; i++){
                items.addProperty(String.valueOf(i), ManageRedisData.getInventoryItemString(uuid ,i));
            }
            String name = MainManageData.inventoriesObjects.get(uuid).getInvName();
            boolean web = MainManageData.inventoriesObjects.get(uuid).isIsWeb();
            ManageMysqlData.saveVirtualInventoryInDatabase(player.getUniqueId().toString(), player.getName(), uuid, items.toString(), name,size, web);
        }
    }

    public static void savePlayerInventoryToDatabase(VirtualInventory vinv, Player player) {
        Gson gson = new Gson();

        JsonObject items = new JsonObject();
        for (int i = 0; i < 27 ; i++){
            items.addProperty(String.valueOf(i), gson.toJson(ManageRedisData.getInventoryItem(vinv ,i).serialize()));
        }
        ManageMysqlData.saveVirtualInventoryInDatabase(player.getUniqueId().toString(), player.getName(), vinv.getInvUuid(), items.toString(), vinv.getInvName(),vinv.getSize(), vinv.isIsWeb());
    }

    public static <T> int indexOf(T[] arr, T val) {
        return Arrays.asList(arr).indexOf(val);
    }


}
