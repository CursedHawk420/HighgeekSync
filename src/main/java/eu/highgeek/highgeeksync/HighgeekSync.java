package eu.highgeek.highgeeksync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import eu.highgeek.highgeeksync.commands.ChannelCommand;
import eu.highgeek.highgeeksync.commands.VirtualInventoryCommand;
import eu.highgeek.highgeeksync.data.sql.controllers.DiscordLinkingCodeController;
import eu.highgeek.highgeeksync.features.chat.ChannelManager;
import eu.highgeek.highgeeksync.features.chat.ChannelMenuListener;
import eu.highgeek.highgeeksync.features.serverstatus.ServerStatus;
import eu.highgeek.highgeeksync.features.virtualinventories.InventoriesManager;
import eu.highgeek.highgeeksync.features.virtualinventories.VirtualInventoriesListener;
import eu.highgeek.highgeeksync.listeners.*;
import eu.highgeek.highgeeksync.models.HighgeekPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;

import eu.highgeek.highgeeksync.config.FileConfig;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.data.sql.controllers.VirtualInventoryController;
import lombok.Getter;

public final class HighgeekSync extends JavaPlugin {

    public Server server = this.getServer();
    public final Logger logger = this.getLogger();
    public FileConfig config;

    @Getter
    private static HighgeekSync instance;
    @Getter
    private static RedisManager redisManager;
    @Getter
    private static VirtualInventoryController virtualInventoryController;
    @Getter
    private static DiscordLinkingCodeController discordLinkingCodeController;
    @Getter
    private final HashMap<String, HighgeekPlayer> highgeekPlayers = new HashMap<String, HighgeekPlayer>();
    @Getter
    private static ChannelManager channelManager;
    @Getter
    private static ProtocolManager protocolManager;
    @Getter
    private ServerStatus serverStatus;
    @Getter
    private static InventoriesManager inventoriesManager;

	private SessionFactory sessionFactory;

    public void init(){
        HighgeekSync.instance = this;
        checkDependencies();

        //Config
        config = new FileConfig(this);
        protocolManager = ProtocolLibrary.getProtocolManager();

        //Init data
        initHibernate();
        HighgeekSync.redisManager = new RedisManager();

        //Init services
        HighgeekSync.channelManager = new ChannelManager(redisManager);
        HighgeekSync.inventoriesManager = new InventoriesManager();
        this.serverStatus = new ServerStatus(redisManager);

        //Register events
        server.getPluginManager().registerEvents(new PlayerJoinListener(redisManager, channelManager),this);
        server.getPluginManager().registerEvents(new ChatListener(redisManager, channelManager),this);
        server.getPluginManager().registerEvents(new RedisChatListener(channelManager),this);
        server.getPluginManager().registerEvents(new VirtualInventoriesListener(redisManager, inventoriesManager), this);
        server.getPluginManager().registerEvents(new ChannelMenuListener(channelManager),this);
        server.getPluginManager().registerEvents(new StatsListener(redisManager), this);
        server.getPluginManager().registerEvents(new PlayerLeaveListener(),this);

        server.getPluginManager().registerEvents(serverStatus,this);


        CommandAPI.registerCommand(ChannelCommand.class);
        CommandAPI.registerCommand(VirtualInventoryCommand.class);
    }

    private void initHibernate(){
        //Hibernate
        this.sessionFactory = config.getSessionFactory();
        //Hibernate controllers
        virtualInventoryController = new VirtualInventoryController(this.sessionFactory);
        discordLinkingCodeController = new DiscordLinkingCodeController(this.sessionFactory);

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

    @Override
    public void onEnable() {
        try {
            init();
        }catch (Exception e){
            logger.warning("An error prevented HighgeekSync to load correctly: "+ e.toString());
        }
    }

    @Override
    public void onDisable() {
        serverStatus.onShutDown();
        redisManager.stopSubscriber();
    }

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        if (this.getDataFolder().mkdir()) {
            this.logger.info("Plugin folder got created.");
        }
        this.saveResource("config.yml", false);
    }


}
