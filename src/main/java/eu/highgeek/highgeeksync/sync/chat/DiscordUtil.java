package eu.highgeek.highgeeksync.sync.chat;

import java.time.Instant;
import java.util.HashMap;

import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.entity.Player;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.data.sql.MysqlDiscord;

public class DiscordUtil {

    public static HashMap<Player, String> codeMap = new HashMap<>();

    public static void generateLinkingCode(Player player){
        String linkingCode = RandomStringUtils.randomAlphanumeric(6);
        MysqlDiscord.saveLinkCode(player.getUniqueId().toString(), linkingCode, Instant.now().toEpochMilli() + 172800000);
        
        codeMap.put(player, linkingCode);

    }

    public static void loadLinkingCode(Player player){
        String linkingCode = MysqlDiscord.getLinkingCode(player);
        if (linkingCode != null){
            codeMap.put(player, linkingCode);
        }else{
            Main.logger.warning("Failed to load linkig code for player: " + player.getName());
        }
    }
}
