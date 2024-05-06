package eu.highgeek.highgeeksync;

import eu.highgeek.highgeeksync.listener.*;
import eu.highgeek.highgeeksync.util.ConfigManager;
import eu.highgeek.highgeeksync.util.Updater;

import eu.highgeek.highgeeksync.websync.chat.DiscordListener;
import eu.highgeek.highgeeksync.websync.chat.VentureChatListener;
import eu.highgeek.highgeeksync.websync.command.DefaultInvCommand;
import eu.highgeek.highgeeksync.websync.data.ManageMysqlData;
import eu.highgeek.highgeeksync.websync.data.ManageRedisData;
import eu.highgeek.highgeeksync.websync.data.RedisEventSetup;
import eu.highgeek.highgeeksync.websync.listeners.RedisListener;
import eu.highgeek.highgeeksync.websync.listeners.VirtualInventoryListener;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

import java.util.logging.Level;
import java.util.logging.Logger;

import static eu.highgeek.highgeeksync.MainManageData.inventoriesIds;
import static eu.highgeek.highgeeksync.MainManageData.inventoriesObjects;

public class Main extends JavaPlugin {

    public static Main main;
    public static Logger logger;
    public static Boolean isStopping = false;
    public static Jedis redisConnection;


    //public static JDA jda;



    public void registerListener() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents((Listener) new JoinListener(), (Plugin) this);
        pluginManager.registerEvents((Listener) new QuitListener(), (Plugin) this);
        pluginManager.registerEvents((Listener) new DeathListener(), (Plugin) this);
        pluginManager.registerEvents((Listener) new CommandListener(), (Plugin) this);

        pluginManager.registerEvents((Listener) new VirtualInventoryListener(), (Plugin) this);
        pluginManager.registerEvents((Listener) new RedisListener(), (Plugin) this);
        pluginManager.registerEvents((Listener) new VentureChatListener(), (Plugin) this);
    }

    public static Boolean isUpdateAvailable() {
        /*try {
            URL url = new URL("https://raw.githubusercontent.com/SphinxHD/MySQL-Sync/main/newest-version");
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String version = bufferedReader.readLine();
            bufferedReader.close();
            return !(version.equalsIgnoreCase(ConfigManager.getString("version")));
        } catch (IOException ignored) {
            return false;
        }*/
        return false; //added always false return
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        main = this;
        logger = this.getLogger();

        saveDefaultConfig();
        ConfigManager.reload();

        DiscordSRV.api.subscribe(new DiscordListener());
        //jda.addEventListener(new DiscordListener());


        redisConnection = ManageRedisData.setupRedis();

        RedisEventSetup.listenerStarter(main, ManageRedisData.setupRedis());

        registerListener();
        MainManageData.initialize();
        Updater.checkForMySQLUpdate();
        Bukkit.getPluginCommand("vinv").setExecutor((CommandExecutor) new DefaultInvCommand());
        Bukkit.getPluginCommand("hg").setExecutor((CommandExecutor) new MainCommand());
        Bukkit.getPluginCommand("hg").setTabCompleter(new MainCommandTabComplete());


        inventoriesIds = ManageMysqlData.loadAllVirtualInventoriesUuids();
        inventoriesObjects = ManageMysqlData.loadAllVirtualInventoriesObjects();

        Updater.checkForUpdate();
        if (isUpdateAvailable()) {
            logger.log(Level.WARNING, "MySQL Sync is not up to date. Please download the newest version on Spigot: https://www.spigotmc.org/resources/mysql-sync.101554/");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        //getServer().getScheduler().cancelTasks(this);
        if (isStopping) return;
        isStopping = true;
        //RedisEventSetup.listener.disconnect();
        //RedisEventSetup.listenertask.cancel();
        MainManageData.startShutdown();
        Bukkit.getServer().getScheduler().cancelTasks(main);
    }
}
