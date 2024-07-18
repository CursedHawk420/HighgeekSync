package eu.highgeek.highgeeksync.sync.adapters;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.saicone.rtag.item.ItemTagStream;


public class ItemStackAdapter {

    public static String itemStackToString(ItemStack itemStack){
        String snbt = ItemTagStream.INSTANCE.toString(itemStack);
        return snbt;
    }

    public static ItemStack stringToItemStack(String snbt){
        try {
            ItemStack itemStack = ItemTagStream.INSTANCE.fromString(snbt);
            return itemStack;
        }catch (Exception e){
            return new ItemStack(Material.AIR);
        }
    }

    public static ItemStack returnErrorBarrier(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Invalid Item");
        meta.setLore(Arrays.asList("Probably modded item.", "If not contact admin."));
        item.setItemMeta(meta);
        return item;
    }
}
