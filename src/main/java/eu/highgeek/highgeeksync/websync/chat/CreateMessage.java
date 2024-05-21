package eu.highgeek.highgeeksync.websync.chat;

import java.util.Collection;

import org.bukkit.Bukkit;

import eu.highgeek.highgeeksync.util.ConfigManager;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.WebhookUtil;
import net.md_5.bungee.api.ChatColor;


public class CreateMessage {

    //private static EmbedBuilder builder = new EmbedBuilder();

    public static void createChatMessage(Message message){

        //Main.logger.warning("createChatMessage uuid: " + message.getUuid());

        //Main.logger.warning("createChatMessage getOfflinePlayer: " + player.getName());

        String channelId = DiscordSRV.getPlugin().getChannels().get(message.getChannel());
        //Main.logger.warning("channelId: " + channelId);

        TextChannel textChannel = DiscordUtil.getTextChannelById(channelId);
        //Main.logger.warning("channelName: " + textChannel.getName());

        //builder.setAuthor(message.getUsername());
        //MessageEmbed embed = builder.build();
        Collection<? extends MessageEmbed> embeds = null;

        //User user  = luckPerms.getUserManager().getUser(message.getUsername());

        //String prefix = user.getCachedData().getMetaData().getPrefix();


        String text = message.getChannelprefix() + " " + message.getPrefix() + message.getUsername() + message.getSuffix() +" » " + message.getMessage();


        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', text));


        if (ConfigManager.getBoolean("chat.listentodiscord")){
            String name = message.getPrefix() + message.getUsername() + message.getSuffix();
            name = name.replaceAll("&.", "");
            String mess =  message.getMessage();
            mess = mess.replaceAll("&.", "");
            WebhookUtil.deliverMessage(textChannel, name, "https://highgeek.eu/api/skins/playerhead/"+message.getUsername(),mess, embeds);
        }

        //AsyncPlayerChatEvent chatEvent = new AsyncPlayerChatEvent(true, player, message.getMessage(), players);

    }


}
