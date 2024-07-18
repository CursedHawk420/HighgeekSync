package eu.highgeek.highgeeksync.commands;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import dev.jorel.commandapi.annotations.Permission;
import dev.jorel.commandapi.annotations.Subcommand;
import dev.jorel.commandapi.annotations.arguments.AStringArgument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import eu.highgeek.highgeeksync.sync.inventory.InventoryManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("vinv")
public class VinvCommand {

    @Default
    public static void vinv(CommandSender sender) {
        if (sender instanceof Player player){
            InventoryManager.openPlayerDefaultInventory(player);
        }
        else {
            sender.sendMessage("You must be player to run this command!");
        }
    }


    @Subcommand("create")
    @Permission("highgeek.vinv.create")
    public static void createVinv(CommandSender sender, @AStringArgument String vinvName) {
        if (sender instanceof Player player){
            InventoryManager.openSpecificInventory(player, InventoryManager.createVirtualInventory(player, vinvName).InvUuid);
        }
        else {
            sender.sendMessage("You must be player to run this command!");
        }

    }

    @Subcommand("open")
    @Permission("highgeek.vinv.open")
    public static void openVinv(CommandSender sender, @AStringArgument String vinvName) {
        if (sender instanceof Player player){
            InventoryManager.openSpecificInventory(player, vinvName);
        }
        else {
            sender.sendMessage("You must be player to run this command!");
        }

    }
}
