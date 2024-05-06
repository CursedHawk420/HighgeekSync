package eu.highgeek.highgeeksync.websync.command;

import eu.highgeek.highgeeksync.websync.inventory.VirtualInventoryManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DefaultInvCommand implements CommandExecutor {
    VirtualInventoryManager inventoryManager = new VirtualInventoryManager();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player) {
            inventoryManager.openInventory(player, "default");
        }
        return true;
    }
}
