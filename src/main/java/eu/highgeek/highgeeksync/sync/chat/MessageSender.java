package eu.highgeek.highgeeksync.sync.chat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.objects.Message;
import eu.highgeek.highgeeksync.utils.ConfigManager;
import eu.highgeek.highgeeksync.utils.VersionHandler;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageSender {


    public static PacketContainer createChatPacket(Message message){

        final PacketContainer container;
        String json = convertToJson(message);

        if (VersionHandler.isAtLeast_1_20_4()) { // 1.20.4+
            container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
            container.getChatComponents().write(0, WrappedChatComponent.fromJson(json));
            container.getBooleans().write(0, false);
        } else if (VersionHandler.isAbove_1_19()) { // 1.19.1 -> 1.20.3
            container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
            container.getStrings().write(0, json);
            container.getBooleans().write(0, false);
        } else if (VersionHandler.isUnder_1_19()) { // 1.7 -> 1.19
            WrappedChatComponent component = WrappedChatComponent.fromJson(json);
            container = new PacketContainer(PacketType.Play.Server.CHAT);
            container.getModifier().writeDefaults();
            container.getChatComponents().write(0, component);
        } else { // 1.19
            container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
            container.getStrings().write(0, json);
            container.getIntegers().write(0, 1);
        }
        return container;
    }

    public static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public  static String format(String msg){
        Main.logger.warning("Before format: \n" + msg);
        Matcher match = pattern.matcher(msg);
        while (match.find()){
            String color = msg.substring(match.start(), match.end());
            msg = msg.replaceAll(HEX_COLOR_CODE_PREFIX, BUKKIT_COLOR_CODE_PREFIX_CHAR + BUKKIT_HEX_COLOR_CODE_PREFIX);
            //msg = msg.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
        }
        Main.logger.warning("After format: \n" + msg);
        return msg;
    }

    public static String convertToJson(Message message) {
        message.setMessage(escapeJsonChars(message.getMessage()));
        String json = "[\"\",{\"text\":\"\",\"extra\":[";
        json += convertLinks(format(channelMessageBuilder(message)));
        json += "]}";
        json += "," + convertLinks(format(message.getMessage().replaceAll("&", "§")));
        json += "]";
        Main.logger.warning("Json msg: \n" + json);
        return json;
    }

    public static String channelMessageBuilder(Message message){
        String toSend = "&8["+message.getChannelprefix()+"&8@"+message.getPrettyservername()+"&8] "+ message.getPrefix() + message.getNickname() + message.getSuffix() + ": ";
        return  toSend.replaceAll("&", "§");
    }

    private static String escapeJsonChars(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static String convertLinks(String s) {
        String remaining = s;
        String temp = "";
        int indexLink = -1;
        int indexLinkEnd = -1;
        String link = "";
        String lastCode = DEFAULT_COLOR_CODE;
        do {
            Pattern pattern = Pattern.compile(
                    "([a-zA-Z0-9" + BUKKIT_COLOR_CODE_PREFIX + "\\-:/]+\\.[a-zA-Z/0-9" + BUKKIT_COLOR_CODE_PREFIX
                            + "\\-:_#]+(\\.[a-zA-Z/0-9." + BUKKIT_COLOR_CODE_PREFIX + "\\-:;,#\\?\\+=_]+)?)");
            Matcher matcher = pattern.matcher(remaining);
            if (matcher.find()) {
                indexLink = matcher.start();
                indexLinkEnd = matcher.end();
                link = remaining.substring(indexLink, indexLinkEnd);
                temp += convertToJsonColors(lastCode + remaining.substring(0, indexLink)) + ",";
                lastCode = getLastCode(lastCode + remaining.substring(0, indexLink));
                String https = "";
                if (ChatColor.stripColor(link).contains("https://"))
                    https = "s";
                temp += convertToJsonColors(lastCode + link,
                        ",\"underlined\":" + underlineURLs()
                                + ",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"http" + https + "://"
                                + ChatColor.stripColor(link.replace("http://", "").replace("https://", ""))
                                + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":["
                                + convertToJsonColors(lastCode + link) + "]}}")
                        + ",";
                lastCode = getLastCode(lastCode + link);
                remaining = remaining.substring(indexLinkEnd);
            } else {
                temp += convertToJsonColors(lastCode + remaining);
                break;
            }
        } while (true);
        return temp;
    }
    public static String convertToJsonColors(String s) {
        return convertToJsonColors(s, "");
    }
    public static String underlineURLs() {
        final boolean configValue = true;
        if (VersionHandler.isAtLeast_1_20_4()) {
            return String.valueOf(configValue);
        } else {
            return "\"" + configValue + "\"";
        }
    }
    private static String convertToJsonColors(String s, String extensions) {
        String remaining = s;
        String temp = "";
        int indexColor = -1;
        int indexNextColor = -1;
        String color = "";
        String modifier = "";
        boolean bold = false;
        boolean obfuscated = false;
        boolean italic = false;
        boolean strikethrough = false;
        boolean underlined = false;
        String previousColor = "";
        int colorLength = LEGACY_COLOR_CODE_LENGTH;
        do {
            if (remaining.length() < LEGACY_COLOR_CODE_LENGTH) {
                temp = "{\"text\":\"" + remaining + "\"},";
                break;
            }
            modifier = "";
            indexColor = remaining.indexOf(BUKKIT_COLOR_CODE_PREFIX);
            previousColor = color;

            color = remaining.substring(1, indexColor + LEGACY_COLOR_CODE_LENGTH);
            if (color.equals(BUKKIT_HEX_COLOR_CODE_PREFIX)) {
                if (remaining.length() >= HEX_COLOR_CODE_LENGTH) {
                    color = HEX_COLOR_CODE_PREFIX
                            + remaining.substring(LEGACY_COLOR_CODE_LENGTH, indexColor + HEX_COLOR_CODE_LENGTH)
                            .replace(BUKKIT_COLOR_CODE_PREFIX, "");
                    colorLength = HEX_COLOR_CODE_LENGTH;
                    bold = false;
                    obfuscated = false;
                    italic = false;
                    strikethrough = false;
                    underlined = false;
                }
            } else if (!color.matches("[0123456789abcdefABCDEF]")) {
                switch (color) {
                    case "l":
                    case "L": {
                        bold = true;
                        break;
                    }
                    case "k":
                    case "K": {
                        obfuscated = true;
                        break;
                    }
                    case "o":
                    case "O": {
                        italic = true;
                        break;
                    }
                    case "m":
                    case "M": {
                        strikethrough = true;
                        break;
                    }
                    case "n":
                    case "N": {
                        underlined = true;
                        break;
                    }
                    case "r":
                    case "R": {
                        bold = false;
                        obfuscated = false;
                        italic = false;
                        strikethrough = false;
                        underlined = false;
                        color = "f";
                        break;
                    }
                }
                if (!color.equals("f"))
                    color = previousColor;
                if (color.length() == 0)
                    color = "f";
            } else {
                bold = false;
                obfuscated = false;
                italic = false;
                strikethrough = false;
                underlined = false;
            }
            if (bold)
                if (VersionHandler.isAtLeast_1_20_4()) {
                    modifier += ",\"bold\":true";
                } else {
                    modifier += ",\"bold\":\"true\"";
                }
            if (obfuscated)
                if (VersionHandler.isAtLeast_1_20_4()) {
                    modifier += ",\"obfuscated\":true";
                } else {
                    modifier += ",\"obfuscated\":\"true\"";
                }
            if (italic)
                if (VersionHandler.isAtLeast_1_20_4()) {
                    modifier += ",\"italic\":true";
                } else {
                    modifier += ",\"italic\":\"true\"";
                }
            if (underlined)
                if (VersionHandler.isAtLeast_1_20_4()) {
                    modifier += ",\"underlined\":true";
                } else {
                    modifier += ",\"underlined\":\"true\"";
                }
            if (strikethrough)
                if (VersionHandler.isAtLeast_1_20_4()) {
                    modifier += ",\"strikethrough\":true";
                } else {
                    modifier += ",\"strikethrough\":\"true\"";
                }
            remaining = remaining.substring(colorLength);
            colorLength = LEGACY_COLOR_CODE_LENGTH;
            indexNextColor = remaining.indexOf(BUKKIT_COLOR_CODE_PREFIX);
            if (indexNextColor == -1) {
                indexNextColor = remaining.length();
            }
            temp += "{\"text\":\"" + remaining.substring(0, indexNextColor) + "\",\"color\":\""
                    + hexidecimalToJsonColorRGB(color) + "\"" + modifier + extensions + "},";
            remaining = remaining.substring(indexNextColor);
        } while (remaining.length() > 1 && indexColor != -1);
        if (temp.length() > 1)
            temp = temp.substring(0, temp.length() - 1);
        return temp;
    }
    public static String getLastCode(String s) {
        String ts = "";
        char[] ch = s.toCharArray();
        for (int a = 0; a < s.length() - 1; a++) {
            if (String.valueOf(ch[a + 1]).matches("[lkomnLKOMN]") && ch[a] == BUKKIT_COLOR_CODE_PREFIX_CHAR) {
                ts += String.valueOf(ch[a]) + ch[a + 1];
                a++;
            } else if (String.valueOf(ch[a + 1]).matches("[0123456789abcdefrABCDEFR]")
                    && ch[a] == BUKKIT_COLOR_CODE_PREFIX_CHAR) {
                ts = String.valueOf(ch[a]) + ch[a + 1];
                a++;
            } else if (ch[a + 1] == 'x' && ch[a] == BUKKIT_COLOR_CODE_PREFIX_CHAR) {
                if (ch.length > a + 13) {
                    if (String.valueOf(ch[a + 3]).matches("[0123456789abcdefABCDEF]")
                            && String.valueOf(ch[a + 5]).matches("[0123456789abcdefABCDEF]")
                            && String.valueOf(ch[a + 7]).matches("[0123456789abcdefABCDEF]")
                            && String.valueOf(ch[a + 9]).matches("[0123456789abcdefABCDEF]")
                            && String.valueOf(ch[a + 11]).matches("[0123456789abcdefABCDEF]")
                            && String.valueOf(ch[a + 13]).matches("[0123456789abcdefABCDEF]")
                            && ch[a + 2] == BUKKIT_COLOR_CODE_PREFIX_CHAR && ch[a + 4] == BUKKIT_COLOR_CODE_PREFIX_CHAR
                            && ch[a + 6] == BUKKIT_COLOR_CODE_PREFIX_CHAR && ch[a + 8] == BUKKIT_COLOR_CODE_PREFIX_CHAR
                            && ch[a + 10] == BUKKIT_COLOR_CODE_PREFIX_CHAR
                            && ch[a + 12] == BUKKIT_COLOR_CODE_PREFIX_CHAR) {
                        ts = String.valueOf(ch[a]) + ch[a + 1] + ch[a + 2] + ch[a + 3] + ch[a + 4] + ch[a + 5]
                                + ch[a + 6] + ch[a + 7] + ch[a + 8] + ch[a + 9] + ch[a + 10] + ch[a + 11] + ch[a + 12]
                                + ch[a + 13];
                        a += 13;
                    }
                }
            }
        }
        return ts;
    }
    private static String hexidecimalToJsonColorRGB(String c) {
        if (c.length() == 1) {
            switch (c) {
                case "0":
                    return "black";
                case "1":
                    return "dark_blue";
                case "2":
                    return "dark_green";
                case "3":
                    return "dark_aqua";
                case "4":
                    return "dark_red";
                case "5":
                    return "dark_purple";
                case "6":
                    return "gold";
                case "7":
                    return "gray";
                case "8":
                    return "dark_gray";
                case "9":
                    return "blue";
                case "a":
                case "A":
                    return "green";
                case "b":
                case "B":
                    return "aqua";
                case "c":
                case "C":
                    return "red";
                case "d":
                case "D":
                    return "light_purple";
                case "e":
                case "E":
                    return "yellow";
                case "f":
                case "F":
                    return "white";
                default:
                    return "white";
            }
        }
        if (isValidHexColor(c)) {
            return c;
        }
        return "white";
    }
    public static boolean isValidHexColor(String color) {
        Pattern pattern = Pattern.compile("(^&?#[0-9a-fA-F]{6}\\b)");
        Matcher matcher = pattern.matcher(color);
        return matcher.find();
    }

    public static final int LEGACY_COLOR_CODE_LENGTH = 2;
    public static final int HEX_COLOR_CODE_LENGTH = 8;
    public static final String HEX_COLOR_CODE_PREFIX = "#";
    public static final char BUKKIT_COLOR_CODE_PREFIX_CHAR = '\u00A7';
    public static final String BUKKIT_COLOR_CODE_PREFIX = String.valueOf(BUKKIT_COLOR_CODE_PREFIX_CHAR);
    public static final String BUKKIT_HEX_COLOR_CODE_PREFIX = "x";
    public static final String DEFAULT_COLOR_CODE = BUKKIT_COLOR_CODE_PREFIX + "f";

    private static final Pattern LEGACY_CHAT_COLOR_DIGITS_PATTERN = Pattern.compile("&([0-9])");
    private static final Pattern LEGACY_CHAT_COLOR_PATTERN = Pattern.compile(
            "(?<!(&x(&[a-fA-F0-9]){5}))(?<!(&x(&[a-fA-F0-9]){4}))(?<!(&x(&[a-fA-F0-9]){3}))(?<!(&x(&[a-fA-F0-9]){2}))(?<!(&x(&[a-fA-F0-9]){1}))(?<!(&x))(&)([0-9a-fA-F])");

    private static final Pattern PLACEHOLDERAPI_PLACEHOLDER_PATTERN = Pattern.compile("\\{([^\\{\\}]+)\\}");

    public static final long MILLISECONDS_PER_DAY = 86400000;
    public static final long MILLISECONDS_PER_HOUR = 3600000;
    public static final long MILLISECONDS_PER_MINUTE = 60000;
    public static final long MILLISECONDS_PER_SECOND = 1000;

    public static final String DEFAULT_MESSAGE_SOUND = "ENTITY_PLAYER_LEVELUP";
    public static final String DEFAULT_LEGACY_MESSAGE_SOUND = "LEVEL_UP";


}