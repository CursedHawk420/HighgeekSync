package eu.highgeek.highgeeksync.websync.adapters.item;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;

public class ItemStackAdapter {

    public static String itemStackToString(ItemStack itemStack){
        if (itemStack.getType() == Material.AIR){
            return "{}";
        }
        return NBT.itemStackToNBT(itemStack).toString();
    }

    public static ItemStack stringToItemStack(String json){
        try{
            ReadWriteNBT nbt = NBT.parseNBT(json);
            if (nbt == null){
                return returnErrorBarrier();
            }

            ItemStack itemStack = NBT.itemStackFromNBT(nbt);

            if (itemStack != null){
                return itemStack;
            }else{
                return returnErrorBarrier();
            }
            
        }catch(Exception ex){
            return returnErrorBarrier();
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
