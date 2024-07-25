package eu.highgeek.highgeeksync.commands;

import dev.jorel.commandapi.annotations.Command;
import dev.jorel.commandapi.annotations.Default;
import eu.highgeek.highgeeksync.data.sql.MysqlDiscord;
import eu.highgeek.highgeeksync.objects.StatusCode;
import eu.highgeek.highgeeksync.sync.inventory.InventoryManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Random;

@Command("link")
public class LinkCommand {

    @Default
    public static void link(CommandSender sender) {
        if (sender instanceof Player player){
            String code = String.format("%04d", new Random().nextInt(10000));

            StatusCode statusCode = MysqlDiscord.saveLinkCode(player.getUniqueId().toString(), code, Instant.now().toEpochMilli() + 172800000);

            player.sendMessage(statusCode.playerMessage);
            player.sendMessage("Send this code to our Discord bot DMs to link your account!");
        }
        else {
            sender.sendMessage("You must be player to run this command!");
        }
    }
}
