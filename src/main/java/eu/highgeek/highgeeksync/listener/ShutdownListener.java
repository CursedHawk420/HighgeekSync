package eu.highgeek.highgeeksync.listener;

import eu.highgeek.highgeeksync.MainManageData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ShutdownListener implements Listener {

    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        String[] commandArgs = event.getMessage().split(" ");
        if (!commandArgs[0].equalsIgnoreCase("/stop")) return;
        if (!(event.getPlayer().hasPermission("minecraft.command.stop")) && !(event.getPlayer().isOp())) return;
        MainManageData.startShutdown();
    }
}
