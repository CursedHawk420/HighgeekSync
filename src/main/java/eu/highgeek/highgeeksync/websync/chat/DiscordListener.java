package eu.highgeek.highgeeksync.websync.chat;


import eu.highgeek.highgeeksync.Main;
import eu.highgeek.highgeeksync.util.ConfigManager;
import eu.highgeek.highgeeksync.websync.data.ManageRedisData;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.*;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.SubscribeEvent;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import github.scarsz.discordsrv.util.DiscordUtil;
import mineverse.Aust1n46.chat.api.events.VentureChatEvent;
import mineverse.Aust1n46.chat.channel.ChatChannel;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.UUID;


public class DiscordListener extends ListenerAdapter {
    private static LuckPerms luckPerms = LuckPermsProvider.get();
    public static String servername = ConfigManager.getString("chat.servername");
    @Subscribe
    public void onGameChatMessagePostProcessEvent(GameChatMessagePostProcessEvent event){
        //INGAME CHAT
        Event bukkitEvent = event.getTriggeringBukkitEvent();

        VentureChatEvent ventureChatEvent = (VentureChatEvent) bukkitEvent;

        ChatChannel chatChannel = ventureChatEvent.getChannel();

        String username = ventureChatEvent.getUsername();
        //Main.logger.warning( "ventureChatEvent.getUsername(): "+ username);


        String message = ventureChatEvent.getChat();
        //Main.logger.warning( "message: "+ ventureChatEvent.getChat());

        String pg = ventureChatEvent.getPlayerPrimaryGroup();
        //Main.logger.warning( "getPlayerPrimaryGroup: "+ pg);

        String channel = chatChannel.getName().toLowerCase();
        //Main.logger.warning( "channel: "+ channel);
        if (channel.equals("local")){
            channel = servername +"-"+ channel;
        }

        LocalDateTime time = LocalDateTime.now();
        String uuid = "chat:"+channel+":"+time.toString().replaceAll(":", "-")+":"+username;
        //Main.logger.warning( "uuid: "+ uuid);

        String prefix = chatChannel.getPrefix();
        //Main.logger.warning( "ventureChatEvent.getChannel.getPrefix()(): "+ chatChannel.getPrefix());

        String nickname = ventureChatEvent.getNickname();
        //Main.logger.warning( "ventureChatEvent.getNickname(): "+ nickname);

        User user = luckPerms.getUserManager().getUser(event.getPlayer().getUniqueId());
        String Prefix = user.getCachedData().getMetaData().getPrefix();
        String Suffix = user.getCachedData().getMetaData().getSuffix();

        ManageRedisData.addChatEntry(new Message(uuid, username, nickname, message, pg, time.toString(), channel, prefix, "game", servername, Prefix, Suffix, event.getPlayer().getUniqueId()));


        /*String globalJson = ventureChatEvent.getGlobalJSON();
        Main.logger.warning( "ventureChatEvent.getGlobalJSON(): "+ globalJson);
        Main.logger.warning( "ventureChatEvent.getChannel.getChatColor()(): "+ chatChannel.getChatColor());
        Main.logger.warning( "ventureChatEvent.getChannel.getChatColorRaw()(): "+ chatChannel.getChatColorRaw());
        Main.logger.warning( "ventureChatEvent.getChannel.getColor()(): "+ chatChannel.getColor());
        Main.logger.warning( "ventureChatEvent.getChannel.getColorRaw()(): "+ chatChannel.getColorRaw());
        Main.logger.warning( "ventureChatEvent.getChannel.getChatName()(): "+ chatChannel.getName());
        Main.logger.warning( "ventureChatEvent.getChannel.getAlias()(): "+ chatChannel.getAlias());
        Main.logger.warning( "ventureChatEvent.getChannel.getPrefix()(): "+ chatChannel.getPrefix());
        Main.logger.warning( "ventureChatEvent.getChannel.getFormat()(): "+ chatChannel.getFormat());
        Main.logger.warning( "ventureChatEvent.getChat() Message: "+ ventureChatEvent.getChat());*/
    }

    @Inject
    AccountLinkManager accountLinkManager;

    @Subscribe
    public void onDiscordGuildMessageReceivedEvent(DiscordGuildMessageReceivedEvent event){
        if (ConfigManager.getBoolean("chat.listentodiscord")) {
        //DISCORD CHAT RECEIVE
        //Musi být někde bool co toto vypne a zapne pro dany server - pouze jeden server muze odesilat tyto data do redisu

        //dalsi moznosti:
        //onDiscordGuildMessagePreProcessEvent
        //onDiscordGuildMessagePostProcessEvent
        //onDiscordGuildMessagePostBroadcastEvent

            String username = event.getMessage().getAuthor().getName();
            String channel = event.getChannel().getName();
            LocalDateTime time = LocalDateTime.now();
            String uuid = "prechat:" + channel + ":" + time.toString().replaceAll(":", "-") + ":" + username;
            String message = event.getMessage().getContentRaw();
            String pg = event.getMember().getRoles().get(0).getName();
            String prefix = "&8[&2" + channel + "&8@&2Disc&8]";


            try {

            }catch (Exception ex){

            }

            ManageRedisData.addChatEntry(new Message(uuid, username, username, message, pg, time.toString(), channel, prefix, "discord", servername, null, null, null));
        /*Main.logger.warning("onDiscordGuildMessageReceivedEvent triggered ");
        Main.logger.warning(username);*/
        }
    }




//    @Subscribe
//    public void onDiscordGuildMessageSentEvent(DiscordGuildMessageSentEvent event){
//        String name = event.getMessage().getAuthor().getName();
//        Main.logger.warning("onDiscordGuildMessageSentEvent triggered ");
//        Main.logger.warning(name);
//    }
//
//    @Subscribe
//    public void onDiscordGuildMessagePostProcessEvent(DiscordGuildMessagePostProcessEvent event){
//        String name = event.getMessage().getAuthor().getName();
//        Main.logger.warning("onDiscordGuildMessagePostProcessEvent triggered ");
//        Main.logger.warning(name);
//    }
//
//
//
//    @Subscribe
//    public void onDiscordGuildMessagePostBroadcastEvent(DiscordGuildMessagePostBroadcastEvent event){
//        String name = event.getMessage().toString();
//        Main.logger.warning("onDiscordGuildMessagePostBroadcastEvent triggered ");
//        Main.logger.warning(name);
//    }
//    @Subscribe
//    public void onDiscordGuildMessagePreBroadcastEvent(DiscordGuildMessagePreBroadcastEvent event){
//        String name = event.getMessage().toString();
//        Main.logger.warning("onDiscordGuildMessagePreBroadcastEvent triggered ");
//        Main.logger.warning(name);
//    }
//    @Subscribe
//    public void onDiscordGuildMessagePreProcessEvent(DiscordGuildMessagePreProcessEvent event){
//        String name = event.getAuthor().getName();
//        Main.logger.warning("onDiscordGuildMessagePreProcessEvent triggered ");
//        Main.logger.warning(name);
//    }
//
//    @Subscribe
//    public void onVentureChatMessagePostProcessEvent(VentureChatMessagePostProcessEvent event){
//        String name = event.getProcessedMessage();
//        Main.logger.warning("onVentureChatMessagePostProcessEvent triggered ");
//        Main.logger.warning(name);
//    }
//    @Subscribe
//    public void onVentureChatMessagePreProcessEvent(VentureChatMessagePreProcessEvent event){
//        String name = event.getMessage();
//        Main.logger.warning("onVentureChatMessagePreProcessEvent triggered ");
//        Main.logger.warning(name);
//    }
//
//
//    @Subscribe
//    public void onGameChatMessagePreProcessEvent(GameChatMessagePreProcessEvent event){
//        String name = event.getMessage();
//
//        Main.logger.warning("onGameChatMessagePreProcessEvent triggered ");
//        Main.logger.warning(name);
//    }
//
//    @Subscribe
//    public void onWatchdogMessagePostProcessEvent(WatchdogMessagePostProcessEvent event){
//        String name = event.getProcessedMessage();
//        Main.logger.warning("onWatchdogMessagePostProcessEvent triggered ");
//        Main.logger.warning(name);
//    }
//
//    @Subscribe
//    public void onWatchdogMessagePreProcessEvent(WatchdogMessagePreProcessEvent event){
//        String name = event.getMessage();
//        Main.logger.warning("onWatchdogMessagePreProcessEvent triggered ");
//        Main.logger.warning(name);
//    }

}
