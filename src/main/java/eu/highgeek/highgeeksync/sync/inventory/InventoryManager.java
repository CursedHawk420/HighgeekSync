package eu.highgeek.highgeeksync.sync.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.data.sql.MysqlVirtualInventoryManager;
import eu.highgeek.highgeeksync.objects.VirtualInventory;

public class InventoryManager {

    public static HashMap<String , List<UUID>> openedInventories = new HashMap<String , List<UUID>>();

    public static List<VirtualInventory> inventoriesList = new ArrayList<VirtualInventory>();

    public static HashMap<UUID, List<VirtualInventory>> playerInventories = new HashMap<UUID,  List<VirtualInventory>>();


    public static void onPlayerJoin(Player player){
        List<VirtualInventory> playerInventoriesList = getPlayerVirtualInventories(player);
        if(playerInventoriesList.isEmpty()){
            createVirtualInventory(player, "default");
            createWebInventory(player,"default");
        }
        playerInventories.put(player.getUniqueId(), playerInventoriesList);
    }

    public static void onPlayerLeave(Player player){
        InventoryManager.playerInventories.remove(player.getUniqueId());
    }

    public static void openPlayerDefaultInventory(Player player){
        try {
            String invUuid = playerInventories.get(player.getUniqueId()).stream()
                    .filter(inv -> inv.InvName.equals("default"))
                    .findAny()
                    .orElse(null)
                    .InvUuid;

            Main.logger.warning("Opening default inventory: " + invUuid);

            openSpecificInventory(player, invUuid);

        }catch (NullPointerException exception){
            Main.logger.warning("Tried to load invalid default inventory!");
        }
    }

    public static void openSpecificInventory(Player player, final String uuid) {
        VirtualInventory vinv = inventoriesList.stream()
                .filter(inv -> inv.InvUuid.equals(uuid))
                .findAny()
                .orElse(null);


        Inventory inv = Bukkit.createInventory(new VirtualInventoryHolder(uuid, vinv), vinv.getSize(), "§3Virtual Chest " + vinv.getInvName());

        String invPrefix = "vinv:"+vinv.PlayerName+":"+vinv.InvUuid;

        Main.logger.warning("Opening inventory: " + invPrefix);

        for (int i = 0; i < vinv.getSize() ; i++) {
            inv.setItem(i, RedisManager.getItemFromRedis(invPrefix + ":" + i));
        }

       List<UUID> uuids = InventoryManager.openedInventories.get(uuid);
        if (uuids == null){
            uuids = new ArrayList<>();
        }
        uuids.add(player.getUniqueId());
        InventoryManager.openedInventories.put( uuid,uuids);
        player.openInventory(inv);

    }

    public static List<VirtualInventory> getPlayerVirtualInventories(Player player){
        Main.logger.warning("loading invetrories for player: " + player.getUniqueId().toString());
        List<VirtualInventory> invs = inventoriesList.stream()
        .filter(item -> item.OwnerUuid.equals(player.getUniqueId().toString()))
        .collect(Collectors.toList());
        for (VirtualInventory virtualInventory : invs) {
            Main.logger.warning("getPlayerVirtualInventories: " + virtualInventory.InvUuid);
        }
        
        return invs;
    }

    public static VirtualInventory createVirtualInventory(Player player,String name){
        VirtualInventory vinv = new VirtualInventory();

        vinv.setIsWeb(false);
        vinv.setSize(27);
        vinv.setOwnerUuid(player.getUniqueId().toString());
        vinv.setPlayerName(player.getName());
        vinv.setInvUuid(UUID.randomUUID().toString());
        vinv.setInvName(name);

        RedisManager.generateInventoryInRedis(vinv, "vinv");

        MysqlVirtualInventoryManager.saveVirtualInventoryInDatabase(vinv.OwnerUuid, vinv.PlayerName, vinv.InvUuid, "empty", vinv.InvName, vinv.Size, vinv.IsWeb);

        List<VirtualInventory> inventory = getPlayerVirtualInventories(player);
        inventory.add(vinv);
        playerInventories.put(player.getUniqueId(), inventory);
        return vinv;
    }

    public static VirtualInventory createWebInventory(Player player, String name){
        VirtualInventory winv = new VirtualInventory();

        winv.setIsWeb(true);
        winv.setSize(27);
        winv.setOwnerUuid(player.getUniqueId().toString());
        winv.setPlayerName(player.getName());
        winv.setInvUuid(UUID.randomUUID().toString());
        winv.setInvName(name);

        RedisManager.generateInventoryInRedis(winv, "winv");
        MysqlVirtualInventoryManager.saveVirtualInventoryInDatabase(winv.OwnerUuid, winv.PlayerName, winv.InvUuid, "empty", winv.InvName, winv.Size, winv.IsWeb);

        return winv;
    }
}
