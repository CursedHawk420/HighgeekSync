package eu.highgeek.highgeeksync.websync.chat;

import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.util.ConfigManager;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.hooks.VaultHook;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.WebhookUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.context.Context;
import net.luckperms.api.context.ContextManager;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.ChatMetaType;
import net.luckperms.api.node.Node;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;


public class CreateMessage {

    //private static EmbedBuilder builder = new EmbedBuilder();
    private static LuckPerms luckPerms = LuckPermsProvider.get();

    public static void createChatMessage(Message message){

        //Main.logger.warning("createChatMessage uuid: " + message.getUuid());

        OfflinePlayer player = Bukkit.getOfflinePlayer(message.getUsername());

        //Main.logger.warning("createChatMessage getOfflinePlayer: " + player.getName());

        String channelId = DiscordSRV.getPlugin().getChannels().get(message.getChannel());
        //Main.logger.warning("channelId: " + channelId);

        TextChannel textChannel = DiscordUtil.getTextChannelById(channelId);
        //Main.logger.warning("channelName: " + textChannel.getName());

        //builder.setAuthor(message.getUsername());
        //MessageEmbed embed = builder.build();
        Collection<? extends MessageEmbed> embeds = null;

        Group group = luckPerms.getGroupManager().getGroup(message.getPrimarygroup());

        //User user  = luckPerms.getUserManager().getUser(message.getUsername());

        //String prefix = user.getCachedData().getMetaData().getPrefix();




        String prefix = group.getFriendlyName();

        String text = message.getChannelprefix() + " " + prefix + message.getUsername() + message.getMessage();


        Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', text));


        if (ConfigManager.getBoolean("chat.listentodiscord")){
            WebhookUtil.deliverMessage(textChannel, ChatColor.translateAlternateColorCodes('&', prefix + message.getUsername()), "https://highgeek.eu/api/skins/playerhead/"+message.getUsername(), ChatColor.translateAlternateColorCodes('&', message.getMessage()), embeds);
        }

        //AsyncPlayerChatEvent chatEvent = new AsyncPlayerChatEvent(true, player, message.getMessage(), players);

    }


}
