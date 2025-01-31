package eu.highgeek.highgeeksync.config;

import com.google.gson.annotations.Expose;
import eu.highgeek.highgeeksync.HighgeekSync;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.logging.Level;

@Getter
public class FileConfig {

    private final YamlConfiguration yamlConfiguration;

    private String redisIp = "redisIp";

    private int redisPort = 6379;

    public FileConfig(
            final @NotNull HighgeekSync highgeekSync
    ) {
        this.yamlConfiguration = new YamlConfiguration();

        try {
            yamlConfiguration.load(new File(highgeekSync.getDataFolder() + "/redis-config.yml"));
        } catch (
                final Exception exception
        ) {
            highgeekSync.getLogger().log(
                    Level.SEVERE,
                    "Failed to load database-config.yml",
                    exception
            );
        }
    }

    /**
     * Get the host value, or an empty string if it is null.
     *
     * @return the host value or an empty string
     */
    public String getHost() {
        this.redisIp = this.yamlConfiguration.getString("host", "");
        return this.redisIp;
    }

    /**
     * Retrieves the port number. If the port is not set, the default port 3306 is returned.
     *
     * @return  the port number
     */
    public Integer getPort() {
        this.redisPort = this.yamlConfiguration.getInt("port", 3306);
        return this.redisPort;
    }
}
