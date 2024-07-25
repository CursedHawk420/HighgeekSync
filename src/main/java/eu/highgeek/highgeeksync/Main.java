package eu.highgeek.highgeeksync;

import java.util.logging.Logger;

import eu.highgeek.highgeeksync.commands.ChannelCommand;
import eu.highgeek.highgeeksync.commands.LinkCommand;
import eu.highgeek.highgeeksync.objects.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.AsynchronousManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import eu.highgeek.highgeeksync.commands.VinvCommand;
import eu.highgeek.highgeeksync.data.redis.RedisEventListener;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.data.sql.MySql;
import eu.highgeek.highgeeksync.data.sql.MysqlVirtualInventoryManager;
import eu.highgeek.highgeeksync.listeners.ChatListener;
import eu.highgeek.highgeeksync.listeners.DeathListener;
import eu.highgeek.highgeeksync.listeners.JoinListener;
import eu.highgeek.highgeeksync.listeners.QuitListener;
import eu.highgeek.highgeeksync.listeners.VirtualInventoryListener;
import eu.highgeek.highgeeksync.sync.chat.ChatInitializer;
import eu.highgeek.highgeeksync.utils.ConfigManager;
import redis.clients.jedis.Jedis;

public final class Main extends JavaPlugin implements Listener {

    public static Main main;
    public static Logger logger;
    public static Jedis redisConnection;
    public static BukkitTask redisListenerTask;
    public static ProtocolManager protocolManager;

    public void registerListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents((Listener) new JoinListener(), (Plugin) this);
        pluginManager.registerEvents((Listener) new QuitListener(), (Plugin) this);
        pluginManager.registerEvents((Listener) new DeathListener(), (Plugin) this);

        pluginManager.registerEvents((Listener) new ChatListener(), (Plugin) this);
        pluginManager.registerEvents((Listener) new VirtualInventoryListener(), (Plugin) this);
    }

    @Override
    public void onLoad() {
        //CommandAPI.registerCommand(HgCommand.class);
        CommandAPI.onLoad(new CommandAPIBukkitConfig(main));
    }

    @Override
    public void onEnable() {
        main = this;
        logger = this.getLogger();
        saveDefaultConfig();
        ConfigManager.reload();

        checkDependencies();

        protocolManager = ProtocolLibrary.getProtocolManager();


        MySql.initMysql();
        RedisManager.initRedis();


        MysqlVirtualInventoryManager.loadAllVirtualInventoriesObjects();


        registerListener();

        ChatInitializer.channelInitializer();

        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        PlayerList.onShutDown();

        RedisEventListener.listenerStopper();
        Main.redisConnection.disconnect();
        Main.redisConnection.close();
        MySql.disconnectMySQL();
    }

    private void registerCommands(){
        CommandAPI.registerCommand(VinvCommand.class);
        CommandAPI.registerCommand(ChannelCommand.class);
        CommandAPI.registerCommand(LinkCommand.class);
    }

    private void checkDependencies(){
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            logger.warning("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            logger.warning("Could not find ProtocolLib! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
}
