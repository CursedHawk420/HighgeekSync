package eu.highgeek.highgeeksync;

import static eu.highgeek.highgeeksync.MainManageData.*;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.highgeek.highgeeksync.util.ConfigManager;
import eu.highgeek.highgeeksync.websync.inventory.VirtualInventoryManager;

public class MainCommand implements CommandExecutor {
    VirtualInventoryManager inventoryManagerer = new VirtualInventoryManager();

    public boolean onCommand(CommandSender sender, Command cmd, String cmdlable, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("joinvoid")) return true;
        if (ConfigManager.getBoolean("settings.use-permission") && !sender.hasPermission("hg.command")) {
            sender.sendMessage(ConfigManager.getColoredString("messages.lacking-permission").replaceAll("%permission%", "hg.command"));
            return true;
        } else if (args.length > 4 && !(args[0].equalsIgnoreCase("open"))) {
            sender.sendMessage(ConfigManager.getColoredString("messages.help"));
            return true;
        }
        else if (args[0].equalsIgnoreCase("open")) {
            if(sender instanceof Player player && sender.hasPermission("hg.inv.open")) {
                if(playerInventoriesHashMap.containsKey(player.getUniqueId())){
                    ArrayList<String> list = playerInventoriesHashMap.get(player.getUniqueId());
                    if (list.contains(args[1])){
                        inventoryManagerer.openInventory(player, args[1]);
                        return true;
                    }else if (sender.hasPermission("hg.inv.admin") && inventoriesIds.containsKey(args[1])){
                        inventoryManagerer.openInventory(player, args[1]);
                        return true;
                    }
                    sender.sendMessage(ConfigManager.getColoredString("messages.invalidinvid"));
                    return true;
                }
                sender.sendMessage(ConfigManager.getColoredString("messages.playerhasnoinv"));
                return true;
            }
            sender.sendMessage(ConfigManager.getColoredString("messages.provideinvid"));
            return true;

        }
        else if (args[0].equalsIgnoreCase("create")) {
            if(sender instanceof Player player && sender.hasPermission("hg.inv.create")) {
                if (args.length > 3){
                    inventoryManagerer.createInventory(player, args[1], Integer.valueOf(args[2]), args[3]);
                    sender.sendMessage(ConfigManager.getColoredString("messages.invcreated"));
                    return true;
                }else if (args.length > 2){
                    inventoryManagerer.createInventory(player, args[1], Integer.valueOf(args[2]));
                    sender.sendMessage(ConfigManager.getColoredString("messages.invcreated"));
                    return true;
                }else {
                    inventoryManagerer.createInventory(player, "default", 27);
                    sender.sendMessage(ConfigManager.getColoredString("messages.invcreated"));
                    return true;
                }
            }

        } else if (args.length != 1) {
            sender.sendMessage(ConfigManager.getColoredString("messages.help"));
            return true;
        } else if (args[0].equalsIgnoreCase("version")) {
            sender.sendMessage(ConfigManager.getColoredString("messages.version").replaceAll("%version%", ConfigManager.getString("version")));
            return true;
        } else if (args[0].equalsIgnoreCase("dev") || args[0].equalsIgnoreCase("developer")) {
            sender.sendMessage(ConfigManager.getColoredString("messages.developerFront") + "§3CursedHawk420" + ConfigManager.getColoredString("messages.developerBack"));
            return true;
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("hg.reload")) {
                sender.sendMessage(ConfigManager.getColoredString("messages.lacking-permission").replaceAll("%permission%", "hg.reload"));
                return true;
            }
            ConfigManager.reload();
            MainManageData.reload();
            sender.sendMessage(ConfigManager.getColoredString("messages.reload"));
            return true;
        }
        sender.sendMessage(ConfigManager.getColoredString("messages.help"));
        return true;
    }
}
