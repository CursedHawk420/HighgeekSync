package eu.highgeek.highgeeksync;

import java.util.logging.Logger;

import com.comphenix.protocol.AsynchronousManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import eu.highgeek.highgeeksync.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import eu.highgeek.highgeeksync.commands.VinvCommand;
import eu.highgeek.highgeeksync.data.redis.RedisEventListener;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.data.sql.MySql;
import eu.highgeek.highgeeksync.data.sql.MysqlVirtualInventoryManager;
import eu.highgeek.highgeeksync.utils.ConfigManager;
import redis.clients.jedis.Jedis;

public final class Main extends JavaPlugin implements Listener {

    public static Main main;
    public static Logger logger;
    public static Jedis redisConnection;
    public static BukkitTask redisListenerTask;
    public static ProtocolManager protocolManager;
    public static AsynchronousManager asynchronousProtocolManager;

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
        asynchronousProtocolManager = protocolManager.getAsynchronousManager();


        MySql.initMysql();
        RedisManager.initRedis();


        MysqlVirtualInventoryManager.loadAllVirtualInventoriesObjects();


        registerListener();

        CommandAPI.registerCommand(VinvCommand.class);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        RedisEventListener.listenerStopper();
        MySql.disconnectMySQL();
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
