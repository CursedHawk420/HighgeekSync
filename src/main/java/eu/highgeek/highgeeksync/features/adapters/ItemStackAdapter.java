package eu.highgeek.highgeeksync.features.adapters;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.willfp.eco.core.items.Items;
import eu.highgeek.highgeeksync.HighgeekSync;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class ItemStackAdapter {
    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .create();



    public static String toString(ItemStack itemStack){
        if(itemStack.getType() == Material.AIR){
            return "{\"id\":\"minecraft:air\"}";
        }
        return Items.toSNBT(itemStack);
    }

    public static ItemStack fromString(String snbt){
        if (snbt == null || snbt.isBlank() || snbt.isEmpty()){
            return new ItemStack(Material.AIR);
        }
        Map<String, Object> readableMap = gson.fromJson(snbt, Map.class);

        if(readableMap.get("id").equals("minecraft:air")) {
            return new ItemStack(Material.AIR);
        }else {
            try {
                ItemStack itemStack = Items.fromSNBT(snbt);
                if(itemStack.getType() == Material.AIR){
                    return returnErrorBarrier();
                }
                return Objects.requireNonNullElseGet(itemStack, ItemStackAdapter::returnErrorBarrier);
            }catch (Exception e){
                HighgeekSync.getInstance().logger.warning("stringToItemStack exception: " + e.getMessage() );
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
