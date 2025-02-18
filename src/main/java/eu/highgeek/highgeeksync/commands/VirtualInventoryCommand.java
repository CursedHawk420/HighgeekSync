package eu.highgeek.highgeeksync.commands;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("vinv")
public class VirtualInventoryCommand {

    @Default
    public static void vinv(CommandSender sender) {
        if (sender instanceof Player player){
            HighgeekSync.getInstance().getHighgeekPlayers().get(player.getName()).openDefaultInventory();
        }
        else {
            sender.sendMessage("You must be player to run this command!");
        }
    }

//tests
    /*
    @Subcommand("get")
    public static void vinvGet(CommandSender sender) {
        if (sender instanceof Player player){
            player.getInventory().addItem(ItemStackAdapter.fromString(HighgeekSync.getRedisManager().getStringRedis("itemstest")));
            }
        else {
            sender.sendMessage("You must be player to run this command!");
        }
    }

    @Subcommand("set")
    public static void vinvSet(CommandSender sender) {
        if (sender instanceof Player player){
            HighgeekSync.getRedisManager().setStringRedis("itemstest", ItemStackAdapter.toString(player.getItemInHand()));
        }
        else {
            sender.sendMessage("You must be player to run this command!");
        }
    }*/

}
