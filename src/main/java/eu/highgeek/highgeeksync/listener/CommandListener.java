package eu.highgeek.highgeeksync.listener;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.MainManageData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;

public class CommandListener implements Listener {

    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        String[] commandArgs = event.getMessage().split(" ");
        Player player = event.getPlayer();
        if (MainManageData.loadedPlayerData.contains(player)) {
            ArrayList<String> commands = MainManageData.commandHashMap.get(player);
            commands.add(event.getMessage());
            MainManageData.commandHashMap.put(player, commands);
            event.setMessage("/hg joinvoid");
            return;
        }
        if (!commandArgs[0].equalsIgnoreCase("/stop")) return;
        if (!(player.hasPermission("minecraft.command.stop")) && !(player.isOp())) return;
        if (Main.isStopping) return;
        Main.isStopping = true;
        MainManageData.startShutdown();
    }
}
