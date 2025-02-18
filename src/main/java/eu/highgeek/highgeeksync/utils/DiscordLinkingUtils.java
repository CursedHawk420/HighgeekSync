package eu.highgeek.highgeeksync.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.entity.Player;

import java.util.Locale;

public class DiscordLinkingUtils {

    public static String generateLinkingCode(){
        String linkingCode = RandomStringUtils.randomAlphanumeric(6);
        linkingCode = linkingCode.toUpperCase(Locale.ROOT);
        return linkingCode;
    }
}
