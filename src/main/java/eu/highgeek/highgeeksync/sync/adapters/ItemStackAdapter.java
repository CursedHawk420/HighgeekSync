package eu.highgeek.highgeeksync.sync.adapters;

import com.saicone.rtag.Rtag;
import com.saicone.rtag.RtagDeserializer;
import com.saicone.rtag.RtagItem;
import com.saicone.rtag.item.ItemObject;
import com.saicone.rtag.item.ItemTagStream;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;


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
