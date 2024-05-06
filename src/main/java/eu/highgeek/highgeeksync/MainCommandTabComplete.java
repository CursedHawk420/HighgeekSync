package eu.highgeek.highgeeksync;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static eu.highgeek.highgeeksync.MainManageData.playerInventoriesHashMap;

public class MainCommandTabComplete implements TabCompleter {

    public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdlable, String[] args) {
        ArrayList<String> arrayList = new ArrayList<String>();





        if (args[0].equalsIgnoreCase("open")){
            if(sender instanceof Player player) {
                arrayList = playerInventoriesHashMap.get(player.getUniqueId());
                return arrayList;
            }
        }
        if (args.length >= 2) {
            return arrayList;
        }
        arrayList.add("open");
        arrayList.add("create");
        //arrayList.add("help");
        //arrayList.add("version");
        //arrayList.add("dev");
        //arrayList.add("developer");
        arrayList.add("reload");
        return arrayList;
    }
}
