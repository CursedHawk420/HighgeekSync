package eu.highgeek.highgeeksync.sync.adapters;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.highgeek.highgeeksync.Main;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.saicone.rtag.item.ItemTagStream;


public class ItemStackAdapter {
    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static String itemStackToString(ItemStack itemStack){
        if (itemStack.getType() == Material.AIR){
            return "{\"id\":\"minecraft:air\"}";
        }


        Map<String, Object> readableMap = ItemTagStream.INSTANCE.toMap(itemStack);

        return gson.toJson(readableMap);
    }

    public static ItemStack stringToItemStack(String json){
        Map<String, Object> readableMap = gson.fromJson(json, Map.class);
        if(readableMap.get("id").equals("minecraft:air")){
            return new ItemStack(Material.AIR);
        }else {
            try {
                ItemStack itemStack = ItemTagStream.INSTANCE.fromMap(readableMap);
                return itemStack;
            }catch (Exception e){
                Main.logger.warning("stringToItemStack exception: " + e.getMessage() );
                return returnErrorBarrier();
            }
        }
    }

    public static ItemStack returnErrorBarrier(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Invalid Item");
        meta.setLore(Arrays.asList("Item doesn't exists on this server.", "If should contact admin."));
        item.setItemMeta(meta);
        return item;
    }
}
