package eu.highgeek.highgeeksync;

import eu.highgeek.highgeeksync.config.FileConfig;
import eu.highgeek.highgeeksync.data.redis.RedisManager;
import eu.highgeek.highgeeksync.data.sql.DatabaseFactory;
import eu.highgeek.highgeeksync.data.sql.daos.VirtualInventoryDao;
import eu.highgeek.highgeeksync.data.sql.entities.VirtualInventories;
import eu.highgeek.highgeeksync.listeners.PlayerJoinListener;
import eu.highgeek.highgeeksync.listeners.PlayerLeaveListener;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

public final class HighgeekSync extends JavaPlugin {


    public Server server = this.getServer();
    public FileConfig config;
    public final Logger logger = this.getLogger();
    public final Path dataDirectory = Paths.get("plugins/HighgeekSync/");

    @Getter
    private static HighgeekSync instance;
    @Getter
    private static RedisManager redisManager;

    private DatabaseFactory databaseFactory;
    private VirtualInventoryDao virtualInventoryDao;


    private List<VirtualInventories> virtualInventories;

    public void init() throws IOException{
        HighgeekSync.instance = this;

        //Config
        Files.createDirectories(dataDirectory);
        Path configPath = dataDirectory.resolve("HighgeekSync.toml");
        config = new FileConfig(this);

        //Init services
        HighgeekSync.redisManager = new RedisManager();

        this.databaseFactory = new DatabaseFactory(this);
        this.virtualInventoryDao = new VirtualInventoryDao(this);

        this.virtualInventories = this.virtualInventoryDao.findAll();

        //Register events
        server.getPluginManager().registerEvents(new PlayerJoinListener(),this);
        server.getPluginManager().registerEvents(new PlayerLeaveListener(),this);

        for(VirtualInventories i : this.virtualInventories){
            logger.warning(i.getInventoryUuid());
        }

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
