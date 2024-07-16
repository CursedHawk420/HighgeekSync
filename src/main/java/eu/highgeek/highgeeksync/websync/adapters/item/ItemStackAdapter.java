package eu.highgeek.highgeeksync.websync.adapters.item;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import eu.highgeek.highgeeksync.Main;

public class ItemStackAdapter {

    public static String itemStackToString(ItemStack itemStack){
        Main.logger.warning("itemstack to parse: " + itemStack.getType());
        if (itemStack.getType() == Material.AIR){
            return "air";
        }else{
            return NBT.itemStackToNBT(itemStack).toString();
        }
    }

    public static ItemStack stringToItemStack(String json){
        Main.logger.warning("string to parse: " + json);
        if (json == "air"){
            Main.logger.warning("new AIR returned");
            return new ItemStack(Material.AIR);
        }else{

            try{
                ReadWriteNBT nbt = NBT.parseNBT(json);
                if (nbt == null){
                    Main.logger.warning("nbt == null ; barrier");
                    return returnErrorBarrier();
                }
                ItemStack itemStack = NBT.itemStackFromNBT(nbt);
    
                if (itemStack == null){
                    Main.logger.warning("itemStack == null ; barrier");
                    return returnErrorBarrier();
                }else
                if (itemStack.getType() == null){
                    Main.logger.warning("itemStack.getType() == null ; barrier");
                    return returnErrorBarrier();
                }else
                if (itemStack.getType() == Material.AIR){
                    Main.logger.warning("itemStack.getType() == Material.AIR ; barrier");
                    return returnErrorBarrier();
                }else{
                    return itemStack;
                }
                
            }catch(Exception ex){
                Main.logger.warning("Exception ex: " + ex.getStackTrace());
                return returnErrorBarrier();
            }
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
