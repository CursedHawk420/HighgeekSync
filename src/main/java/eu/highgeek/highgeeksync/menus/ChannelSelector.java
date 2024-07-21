package eu.highgeek.highgeeksync.menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import eu.highgeek.highgeeksync.common.Common;
import eu.highgeek.highgeeksync.objects.PlayerSettings;

public class ChannelSelector implements InventoryHolder {
    
    private final Inventory inventory;
    private final Player player;
    private final PlayerSettings playerSettings;

    public ChannelSelector(Player player){
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 9, "Channel Selector");

        this.playerSettings = Common.playerSettings.get(player);

        initializeItems();

        player.openInventory(inventory);
    }


    public void initializeItems() {
        for (String channel : playerSettings.joinedChannels) {
            
        }
    }

    public ItemStack leaveChannelItem(String channel){
        ItemStack itemStack = new ItemStack(Material.RED_WOOL);
        return itemStack;
    }

    public ItemStack joinChannelItem(String channel){
        ItemStack itemStack = new ItemStack(Material.GREEN_WOOL);
        return itemStack;
    }


    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
