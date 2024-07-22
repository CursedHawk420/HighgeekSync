package eu.highgeek.highgeeksync.commands;

import dev.jorel.commandapi.annotations.Alias;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.menus.ChannelSelector;
import eu.highgeek.highgeeksync.sync.inventory.InventoryManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@Command("channel")
@Alias({"ch", "chan", "chat"})
public class ChannelCommand {

    @Default
    public static void channelSelector(CommandSender sender) {
        if (sender instanceof Player player){
            ChannelSelector channelSelector = new ChannelSelector(player);
            //Main.odalitaMenus.openMenu(new ChannelSelector(), player);
        }
        else {
            sender.sendMessage("You must be player to run this command!");
        }
    }
}
