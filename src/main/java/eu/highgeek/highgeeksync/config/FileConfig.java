package eu.highgeek.highgeeksync.config;

import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.data.sql.entities.VirtualInventories;
import org.bukkit.configuration.file.YamlConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.service.ServiceRegistry;
import org.jetbrains.annotations.NotNull;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.io.File;
import java.util.Properties;
import java.util.logging.Level;

public class FileConfig {

    private final YamlConfiguration yamlConfiguration;

    private String redisIp;

    private int redisPort;

    private String dbConnectionString;

    private String dbUsername;

    private String dbPassword;

    private String serverName;

    private String prettyServerName;

    public FileConfig(
            final @NotNull HighgeekSync highgeekSync
    ) {
        this.yamlConfiguration = new YamlConfiguration();

        try {
            yamlConfiguration.load(new File(highgeekSync.getDataFolder() + "/config.yml"));
        } catch (
                final Exception exception
        ) {
            highgeekSync.getLogger().log(
                    Level.SEVERE,
                    "Failed to load config.yml",
                    exception
            );
        }
    }

    /**
     * Get the host value, or an empty string if it is null.
     *
     * @return the host value or an empty string
     */
    public String getRedisIp() {
        this.redisIp = this.yamlConfiguration.getString("redis-ip", "");
        return this.redisIp;
    }

    /**
     * Retrieves the port number. If the port is not set, the default port 6379 is returned.
     *
     * @return  the port number
     */
    public Integer getRedisPort() {
        this.redisPort = this.yamlConfiguration.getInt("redis-port", 6379);
        return this.redisPort;
    }

    /**
     * Retrieves the port number. If the port is not set, the default port 3306 is returned.
     *
     * @return  the port number
     */
    public String getDbConnectionString() {
        this.dbConnectionString = this.yamlConfiguration.getString("db-connectionstring", "jdbc:mysql://localhost:3306/db");
        return this.dbConnectionString;
    }

    /**
     * Retrieves the port number. If the port is not set, the default port 3306 is returned.
     *
     * @return  the port number
     */
    public String getDbUsername() {
        this.dbUsername = this.yamlConfiguration.getString("db-username", "root");
        return this.dbUsername;
    }

    /**
     * Retrieves the port number. If the port is not set, the default port 3306 is returned.
     *
     * @return  the port number
     */
    public String getDbPassword() {
        this.dbPassword = this.yamlConfiguration.getString("db-password", "root");
        return this.dbPassword;
    }

    /**
     * Retrieves the port number. If the port is not set, the default port 3306 is returned.
     *
     * @return  the port number
     */
    public String getPrettyServerName() {
        this.prettyServerName = this.yamlConfiguration.getString("prettyservername", "&2default");
        return this.prettyServerName;
    }

    /**
     * Retrieves the port number. If the port is not set, the default port 3306 is returned.
     *
     * @return  the port number
     */
    public String getServerName() {
        this.serverName = this.yamlConfiguration.getString("servername", "default");
        return this.serverName;
    }



    public Configuration getHibernateConfiguration() {
        Configuration configuration = new Configuration();

        Properties settings = new Properties();
        settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        settings.put(Environment.URL, getDbConnectionString());
        settings.put(Environment.USER, getDbUsername());
        settings.put(Environment.PASS, getDbPassword());
        settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");

        settings.put(Environment.SHOW_SQL, "true");

        settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

        //settings.put(Environment.HBM2DDL_AUTO, "create-drop");

        configuration.setProperties(settings);
        return configuration;
    }


    private static SessionFactory sessionFactory;


    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = getHibernateConfiguration();

                configuration.addAnnotatedClass(VirtualInventories.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}
