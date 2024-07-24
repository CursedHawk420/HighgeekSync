package eu.highgeek.highgeeksync.sync.adapters;

import java.io.*;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.highgeek.highgeeksync.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.NBTComponent;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.willfp.eco.core.items.Items;

import com.saicone.rtag.item.ItemTagStream;
import org.jetbrains.annotations.NotNull;

import static java.nio.charset.StandardCharsets.UTF_8;

//import com.willfp.eco.;;


public class ItemStackAdapter {
    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .create();
/*
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
    */


    public static String itemStackToString(ItemStack itemStack){
        if (itemStack.getType() == Material.AIR){
            return "{\"id\":\"minecraft:air\"}";
        }
        return Items.toSNBT(itemStack);
    }

    public static ItemStack stringToItemStack(String json){
        Map<String, Object> readableMap = gson.fromJson(json, Map.class);

        if(readableMap.get("id").equals("minecraft:air")){
            return new ItemStack(Material.AIR);
        }else {
            try {
                ItemStack itemStack = Items.fromSNBT(json);
                if(itemStack.getType() == Material.AIR){
                    return returnErrorBarrier();
                }
                return Objects.requireNonNullElseGet(itemStack, ItemStackAdapter::returnErrorBarrier);
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
