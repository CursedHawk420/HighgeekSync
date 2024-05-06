package eu.highgeek.highgeeksync.websync.adapters.item;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.mojang.datafixers.functions.PointFreeRule;
import com.sun.jdi.request.StepRequest;
import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.websync.objects.Item;
import net.minecraft.world.item.ItemEnchantedBook;
import net.minecraft.world.item.ItemFireworks;
import org.bukkit.*;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Skull;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.FireworkEffect.Builder;
//import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftMetaItem;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftMetaColorableArmor;
import org.bukkit.FireworkEffect;
//import com.bergerkiller.bukkit.common.inventory.InventoryBaseImpl;

import java.lang.reflect.Method;
import java.util.*;

import static org.bukkit.FireworkEffect.builder;

public class ItemStackAdapter {
    public static final Gson gson = new GsonBuilder().create();
    public static String itemStackToString(ItemStack item) {
        String s = "";
        // If s is empty, means it is not the items above
        if (s.isEmpty()) {

            // If item does not have item meta, return json directly
            if (!item.hasItemMeta()) return gson.toJson(item.serialize());

            // Get item's item meta
            ItemMeta meta = item.getItemMeta();


            // ItemStack to Map-Object
            Map<String, Object> itemMap = item.serialize();

            // Get serialize method
            try {
                // Attempt to retrieve CraftMetaItem from ItemStack's material,
                // but it might be CraftMetaItem's subclass (e.g. CraftMetaFireWork)
                Class<?> craftMetaItemClass = Bukkit.getItemFactory().getItemMeta(item.getType()).getClass();

                // Get serialize method from CraftMetaItem class
                Method serializeMethod;
                try {
                    // Get "public final Map<String, Object> serialize()"
                    serializeMethod = craftMetaItemClass.getDeclaredMethod("serialize");
                } catch (NoSuchMethodException e) {
                    // If no such method, means we are in the sub-class of CraftMetaItem,
                    // we return to superclass and get the method again
                    try {
                        serializeMethod = craftMetaItemClass.getSuperclass().getDeclaredMethod("serialize");
                    }catch (NoSuchMethodException ex){
                        serializeMethod = craftMetaItemClass.getMethod("serialize");
                    }
                }
                Main.logger.warning("Found serialization class: " + serializeMethod.getDeclaringClass().getName() + " and method: " + serializeMethod.getName());
                // Make it accessible as it is a final method
                serializeMethod.setAccessible(true);

                // Invoke "serialize" to serialize ItemMeta to Map-Object with "meta-type" exists
                Map<String, Object> metaMap = (Map<String, Object>) serializeMethod.invoke(meta);
                // Put the map back to serialized itemstack with key "meta" as it is the key to store item meta
                itemMap.put("meta", metaMap);

                // Make the final serialized itemstack to json format
                s = gson.toJson(itemMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return s;
    }

    public static ItemStack stringToItemStack(String s) {
        ItemStack item = null;

        try {
            // Convert ItemStack json to Map-Object format
            Map<String, Object> itemMap = gson.fromJson(s, Map.class);


            // If the deserialized ItemStack does not have "meta" key, means it has no ItemMeta, return the item directly
            try{
                if (!itemMap.containsKey("meta")) return ItemStack.deserialize(gson.fromJson(s, Map.class));
            }catch (IllegalArgumentException exception){
                Main.logger.info("Illegal item tried to by deserialized, changing itemType to barrier\nException:\n" + exception.getMessage());
                return returnErrorBarrier();
            }


            // Extract the ItemMeta individually
            Map<String, Object> metaMap = (Map<String, Object>) itemMap.get("meta");



            // Since the enchants level is Double but only Integer is allowed, replace all Double to Integer
            if (metaMap.get("enchants") != null){
                LinkedTreeMap enchants = (LinkedTreeMap) metaMap.get("enchants");
                enchants.forEach((key, value) -> {
                    if (value instanceof Double) enchants.replace(key, ((Double) value).intValue());
                });
            }

            // Since the enchants level is Double but only Integer is allowed, replace all Double to Integer
            if (metaMap.get("stored-enchants") != null){
                LinkedTreeMap storedenchants = (LinkedTreeMap) metaMap.get("stored-enchants");
                storedenchants.forEach((key, value) -> {
                    if (value instanceof Double) storedenchants.replace(key, ((Double) value).intValue());
                });
            }

            //converting doubles to ints
            if (metaMap.get("Damage") != null){
                if (metaMap.get("Damage") instanceof Double)
                {
                    int value = ((Double) metaMap.get("Damage")).intValue();
                    metaMap.replace("Damage", value);
                }
            }

            //converting doubles to ints
            if (metaMap.get("repair-cost") != null){
                if (metaMap.get("repair-cost") instanceof Double)
                {
                    int value = ((Double) metaMap.get("repair-cost")).intValue();
                    metaMap.replace("repair-cost", value);
                }
            }

            //converting doubles to ints
            if (metaMap.get("power") != null){
                if (metaMap.get("power") instanceof Double)
                {
                    int value = ((Double) metaMap.get("power")).intValue();
                    metaMap.replace("power", value);
                }
            }
            //converting doubles to ints
            if (metaMap.get("map-id") != null){
                if (metaMap.get("map-id") instanceof Double)
                {
                    int value = ((Double) metaMap.get("map-id")).intValue();
                    metaMap.replace("map-id", value);
                }
            }

            //converting types
            List<Map<String, Object>> bannerpattern = null;
            if (metaMap.get("patterns") != null){
                bannerpattern = (List<Map<String, Object>>) metaMap.get("patterns");
                //Main.logger.warning("This is current bannerpattern" + bannerpattern + "");

                ArrayList<Pattern> list = new ArrayList<>();
                for (Map<String, Object> map: bannerpattern
                ) {
                    DyeColor col = DyeColor.valueOf(map.get("color").toString());
                    PatternType type = PatternType.valueOf(map.get("pattern").toString()) ;
                    list.add(new Pattern(col, type));
                }
                metaMap.replace("patterns", list);
                //metaMap.remove("patterns");

            }
            //converting string
            if (metaMap.get("skull-owner") != null) {
                //predelat key string "skull-owner" na "SkullOwner"
                Object value = metaMap.get("skull-owner");
                metaMap.remove("skull-owner");
                metaMap.put("SkullOwner", value);
            }
            //firework effect
            if (metaMap.get("firework-effects") != null){
                ArrayList<Map<String, Object>> effects = (ArrayList<Map<String, Object>>) metaMap.get("firework-effects");
                ArrayList<Object> neweffects = new ArrayList<>();
                for (Map <String, Object> map : effects
                ) {
                    FireworkEffect.Type type = FireworkEffect.Type.valueOf((String)map.get("type"));

                    ArrayList<Map<String, Object>> colors = (ArrayList<Map<String, Object>>) map.get("colors");
                    ArrayList<Color> colorArrayList = new ArrayList<>();
                    for (Map<String, Object> color : colors
                    ) {

                        int alpha = (((Double)color.get("alpha")).intValue() + 256)  % 256;
                        int red = (((Double)color.get("red")).intValue() + 256)  % 256;
                        int green = (((Double)color.get("green")).intValue() + 256)  % 256;
                        int blue = (((Double)color.get("blue")).intValue()  + 256) % 256;
                        Color newcolor;
                        if (alpha == -1){
                            newcolor = Color.fromRGB(red, green, blue);
                        }else {
                            newcolor = Color.fromARGB(alpha, red, green, blue);
                        }
                        colorArrayList.add(newcolor);
                    }


                    ArrayList<Map<String, Object>> fadecolors = (ArrayList<Map<String, Object>>) map.get("fadeColors");
                    if (fadecolors != null){
                        ArrayList<Color> fadecolorArrayList = new ArrayList<>();

                        for (Map<String, Object> color : fadecolors
                        ) {
                            int alpha = (((Double)color.get("alpha")).intValue() + 256) % 256;
                            int red = (((Double)color.get("red")).intValue() + 256) % 256;
                            int green = (((Double)color.get("green")).intValue() + 256 ) % 256;
                            int blue = (((Double)color.get("blue")).intValue() + 256) % 256;

                            Color newcolor;
                            if (alpha == -1){
                                newcolor = Color.fromRGB(red, green, blue);
                            }else {
                                newcolor = Color.fromARGB(alpha, red, green, blue);
                            }
                            fadecolorArrayList.add(newcolor);
                        }
                        FireworkEffect fireworkEffect = builder().flicker((Boolean)map.get("flicker")).trail((Boolean)map.get("trail")).withColor(colorArrayList).withFade(fadecolorArrayList).with(type).build();
                        neweffects.add(fireworkEffect);

                    }else {
                        FireworkEffect fireworkEffect = builder().flicker((Boolean)map.get("flicker")).trail((Boolean)map.get("trail")).withColor(colorArrayList).with(type).build();
                        neweffects.add(fireworkEffect);
                    }
                }
                metaMap.remove("firework-effects");
                metaMap.put("firework-effects", neweffects);
            }

            if (metaMap.get("color") != null){

                Map<String, Object> color = (Map<String, Object>) metaMap.get("color");
                int alpha = (((Double)color.get("alpha")).intValue() + 256) % 256;
                int red = (((Double)color.get("red")).intValue() + 256) % 256;
                int green = (((Double)color.get("green")).intValue() + 256 ) % 256;
                int blue = (((Double)color.get("blue")).intValue() + 256) % 256;
                Color newcolor = Color.fromARGB(alpha, red, green, blue);
                metaMap.remove("color");
                metaMap.put("color", newcolor);
            }


            //Main.logger.warning("This is current metaMap" + metaMap + "");

            if(metaMap.get("attribute-modifiers") != null){

                LinkedTreeMap<String, ArrayList> modifiers = (LinkedTreeMap) metaMap.get("attribute-modifiers");
                modifiers.forEach((k, v) -> {
                    ArrayList<AttributeModifier> attributesList = new ArrayList<>();
                    // Bukkit.broadcastMessage("" + k.getClass().getName() + ":" + k + " " + v.getClass().getName() + ":" + v);

                    v.forEach(val -> {
                        // Bukkit.broadcastMessage("  " + val + " " + val.getClass().getName());

                        TreeMap<String, Object> treeMap = new TreeMap<>();
                        ((LinkedTreeMap) val).forEach((k1, v1) -> {
                            // Bukkit.broadcastMessage("    " + k1.getClass().getName() + ":" + k1 + " " + v1.getClass().getName() + ":" + v1);

                            if (((String) k1).equalsIgnoreCase("uuid"))
                                treeMap.put((String) k1, UUID.fromString((String) v1));
                            if (((String) k1).equalsIgnoreCase("name")) treeMap.put((String) k1, String.valueOf(v1));
                            if (((String) k1).equalsIgnoreCase("amount"))
                                treeMap.put((String) k1, v1);
                            if (((String) k1).equalsIgnoreCase("operation"))
                                treeMap.put((String) k1, AttributeModifier.Operation.valueOf((String) v1));
                            if (((String) k1).equalsIgnoreCase("slot"))
                                treeMap.put((String) k1, EquipmentSlot.valueOf((String) v1));
                        });

                        AttributeModifier modifier = new AttributeModifier((UUID) treeMap.get("uuid"), (String) treeMap.get("name"),
                                (Double) treeMap.get("amount"), (AttributeModifier.Operation) treeMap.get("operation"), (EquipmentSlot) treeMap.get("slot"));
                        attributesList.add(modifier);
                    });
                    modifiers.replace(k, attributesList);
                });
            }

            // Deserialize map into ItemStack, ItemMeta is still not done

            try{
                item = ItemStack.deserialize(itemMap);
            }catch (IllegalArgumentException exception){
                Main.logger.info("Illegal item tried to by deserialized, changing itemType to barrier\nException:\n" + exception.getMessage());
                return returnErrorBarrier();
            }

            // Attempt to retrieve CraftMetaItem from ItemStack's material
            Class<?> craftMetaItemClass = Bukkit.getItemFactory().getItemMeta(item.getType()).getClass();
            Class<?> serializableMetaClass = null;
            //Class[] craftMetaItemClasses = Class.forName("org.bukkit.craftbukkit.v1_20_R1.inventory.CraftMetaItem").getDeclaredClasses();


            // Iterate through sub-classes of CraftMetaItem
            for (Class<?> inner : craftMetaItemClass.getDeclaredClasses()) {
                // Iterate through declared methods
                for (Method method : inner.getDeclaredMethods()) {
                    // If the method name is "deserialize", means we have found it
                    if (method.getName().equalsIgnoreCase("deserialize")) {
                        serializableMetaClass = inner;
                        break;
                    }
                }
            }


            // If no inner class SerializableMeta found, means the craftMetaItemClass above isn't CraftMetaItem,
            // but sub-class of it. e.g. CraftMetaFirework
            if (serializableMetaClass == null) {
                // Iterate through its superclass's sub-classes
                //for (Class<?> inner : Class.forName("org.bukkit.craftbukkit.v1_20_R1.inventory.CraftMetaItem").getDeclaredClasses()) {
                for (Class<?> inner : craftMetaItemClass.getSuperclass().getDeclaredClasses()) {
                    // Iterate through declared methods
                    for (Method method : inner.getDeclaredMethods()) {
                        // If the method name is "deserialize", means we have found it
                        if (method.getName().equalsIgnoreCase("deserialize")) {
                            serializableMetaClass = inner;
                            break;
                        }
                    }
                }
            }

            if (serializableMetaClass == null) {
                for (Class<?> inner : Class.forName("org.bukkit.craftbukkit.v1_20_R1.inventory.CraftMetaItem").getDeclaredClasses()) {
                    for (Method method : inner.getDeclaredMethods()) {
                        // If the method name is "deserialize", means we have found it
                        if (method.getName().equalsIgnoreCase("deserialize")) {
                            serializableMetaClass = inner;
                            break;
                        }
                    }
                }
            }



            // Get the deserialize method inside SerializableMeta
            Method method = serializableMetaClass.getDeclaredMethod("deserialize", Map.class);
            ItemMeta itemMeta = (ItemMeta) method.invoke(serializableMetaClass, metaMap);


            if (metaMap.get("SkullOwner") != null) {
                Map<String, Object> skullownerMap = (Map<String, Object>) metaMap.get("SkullOwner");
                Map<String, Object> skullMap = (Map<String, Object>) skullownerMap.get("profile");
                ArrayList<Map<String, Object>> propertiesMap = (ArrayList<Map<String, Object>>) skullownerMap.get("properties");

                for (Map<String, Object> d: propertiesMap){
                    if (d.get("signature") != null){
                        SkullCreator.mutateItemMetaWithNameAndUUID((SkullMeta) itemMeta, d.get("value").toString(), skullMap.get("name").toString(), skullMap.get("id").toString(), d.get("signature").toString());
                    }
                    else
                    {
                        SkullCreator.mutateItemMetaWithNameAndUUID((SkullMeta) itemMeta, d.get("value").toString(), skullMap.get("name").toString(), skullMap.get("id").toString(), null);
                    }
                }
            }

            // Apply the deserialized ItemMeta to the ItemStack
            item.setItemMeta(itemMeta);

        } catch (Exception e) {
            Main.logger.warning("Could not load item!: \n " + s + ".");
            e.printStackTrace();
        }

        return item;
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
