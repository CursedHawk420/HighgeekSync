package eu.highgeek.highgeeksync;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import eu.highgeek.highgeeksync.config.FileConfig;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.data.sql.entities.InventoryController;
import eu.highgeek.highgeeksync.data.sql.entities.VirtualInventories;
import eu.highgeek.highgeeksync.listeners.PlayerJoinListener;
import eu.highgeek.highgeeksync.listeners.PlayerLeaveListener;
import lombok.Getter;

public final class HighgeekSync extends JavaPlugin {

	private static final String HIBERNATE_CONFIG_FILE_NAME = "hibernate.cfg.xml";

    public Server server = this.getServer();
    public FileConfig config;
    public final Logger logger = this.getLogger();
    public final Path dataDirectory = Paths.get("plugins/HighgeekSync/");

    @Getter
    private static HighgeekSync instance;
    @Getter
    private static RedisManager redisManager;
    
	private SessionFactory sessionFactory;
	private InventoryController inventoryController;


    private List<VirtualInventories> virtualInventories;

    public void init() throws IOException{
        HighgeekSync.instance = this;

        //Config
        Files.createDirectories(dataDirectory);
        Path configPath = dataDirectory.resolve("HighgeekSync.toml");
        config = new FileConfig(this);

        //Init services
        HighgeekSync.redisManager = new RedisManager();


        //Register events
        server.getPluginManager().registerEvents(new PlayerJoinListener(),this);
        server.getPluginManager().registerEvents(new PlayerLeaveListener(),this);

        for(VirtualInventories i : this.virtualInventories){
            logger.warning(i.getInventoryUuid());
        }

    }

    private void initHibernate(){
        
		this.saveResource(HIBERNATE_CONFIG_FILE_NAME, false);
		this.sessionFactory = new Configuration()
				.configure(new File(this.getDataFolder().getAbsolutePath() + "/" + HIBERNATE_CONFIG_FILE_NAME))
				.addAnnotatedClass(VirtualInventories.class)
				.buildSessionFactory();

		this.inventoryController = new InventoryController(this.sessionFactory);
    }


    @Override
    public void onEnable() {
        try {
            init();
        }catch (Exception e){
            logger.warning("An error prevented HighgeekProxy to load correctly: "+ e.toString());
        }

    }

    @Override
    public void onDisable() {
        redisManager.stopSubscriber();
    }

    @Override
    public void onLoad() {
        if (
                this.getDataFolder().mkdir()
        ) this.logger.info("Plugin folder got created.");

        this.saveResource("database-config.yml", false);
        this.saveResource("redis-config.yml", false);
    }


}
