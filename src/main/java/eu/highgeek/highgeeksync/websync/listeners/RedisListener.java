package eu.highgeek.highgeeksync.websync.listeners;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.MainManageData;
import eu.highgeek.highgeeksync.websync.data.ManageRedisData;
import eu.highgeek.highgeeksync.websync.events.RedisSetEvent;
import eu.highgeek.highgeeksync.websync.inventory.VirtualInventoryHolder;
import github.scarsz.discordsrv.dependencies.commons.collections4.BidiMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RedisListener implements Listener {
    @EventHandler
    public void onRedisSetEvent(RedisSetEvent event){
        Main.logger.warning("Custom event fired!");

        ArrayList<UUID> uuids = MainManageData.openedInventories.get(event.getInvUuid());
        for (var item : uuids
             ) {
            Player player = Bukkit.getServer().getPlayer(item);
            Main.logger.warning("Playername with open inventory: " + player.getName());

            ItemStack newItem = ManageRedisData.getInventoryItem(event.getInvUuid(), event.getSlotId());

            player.getOpenInventory().getTopInventory().setItem(event.getSlotId(), newItem);
        }
    }
}
