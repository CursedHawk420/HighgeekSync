package eu.highgeek.highgeeksync.commands;

import dev.jorel.commandapi.annotations.Alias;
import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.features.chat.ChannelMenu;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("channel")
@Alias({"ch", "chan", "chat"})
public class ChannelCommand {

    @Default
    public static void channelMenu(CommandSender sender) {
        if (sender instanceof Player player){
            new ChannelMenu(HighgeekSync.getInstance().getHighgeekPlayers().get(player.getName()));
        }
        else {
            sender.sendMessage("You must be player to run this command!");
        }
    }
}

