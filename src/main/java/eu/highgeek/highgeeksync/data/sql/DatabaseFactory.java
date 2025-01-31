package eu.highgeek.highgeeksync.data.sql;

import com.google.common.reflect.ClassPath;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import eu.highgeek.highgeeksync.HighgeekSync;
import eu.highgeek.highgeeksync.config.DatabaseConfigurationSection;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseFactory implements IDatabaseProvider {

    private Configuration configuration;

    private final Logger logger;

    private final DatabaseConfigurationSection databaseConfigurationSection;

    public DatabaseFactory(
            final @NotNull HighgeekSync highgeekSync
    ) {
        this.databaseConfigurationSection = new DatabaseConfigurationSection(highgeekSync);
        this.logger = highgeekSync.getLogger();
        this.connect();
    }

    /**
     * This method establishes a connection to the database using the configuration settings.
     * It sets up the properties for the database connection and initializes the configuration.
     * @throws Exception if an error occurs during the database connection process.
     */
    @Override
    public void connect() {
        Properties properties = new Properties();
        properties.setProperty(
                Environment.DRIVER,
                "com.mysql.cj.jdbc.Driver"
        );
        properties.setProperty(
                Environment.USER,
                this.databaseConfigurationSection.getUsername()
        );
        properties.setProperty(
                Environment.PASS,
                this.databaseConfigurationSection.getPassword()
        );
        properties.setProperty(
                Environment.HBM2DDL_AUTO,
                this.databaseConfigurationSection.getTableCreation()
        );
        properties.setProperty(
                Environment.URL,
                this.databaseConfigurationSection.getConnectionURL()
        );
        properties.setProperty(
                Environment.SHOW_SQL,
                String.valueOf(this.databaseConfigurationSection.getIsShowSQL())
        );
        properties.setProperty(
                Environment.AUTOCOMMIT,
                "true"
        );
        properties.setProperty(
                Environment.AUTO_CLOSE_SESSION,
                "true"
        );

        this.configuration = new Configuration().addProperties(properties);
        this.includeAnnotatedClasses();
    }


    /**
     * This method includes annotated classes based on the entity annotated class parent path.
     * It uses ClassPath to get the top-level classes and then filters them to include only classes with the @Entity annotation.
     * Once the annotated classes are identified, they are loaded and added to the configuration.
     * @throws Exception if an error occurs during the process of including annotated classes.
     */
    @Override
    public void includeAnnotatedClasses() {
        try {
            ClassPath.from(this.getClass().getClassLoader())
                    .getAllClasses().stream().filter(
                            classInfo -> classInfo.getPackageName().contains(this.databaseConfigurationSection.getEntityAnnotatedClassParentPath()
                            )).toList().stream()
                    .map(ClassPath.ClassInfo::load)
                    .forEach(clazz -> {
                        try {
                            this.getClass().getClassLoader().loadClass(clazz.getName());
                            this.configuration.addAnnotatedClass(clazz);
                        } catch (
                                final Exception exception
                        ) {
                            this.logger.log(
                                    Level.SEVERE,
                                    "An exception occurred while loading the class: " + clazz.getName(),
                                    exception
                            );
                        }
                    });
        } catch (
                final Exception exception
        ) {
            this.logger.log(
                    Level.SEVERE,
                    "An exception occurred while including annotated classes",
                    exception
            );
        }
    }
    @Nullable
    @Override
    public SessionFactory buildSessionFactory() {
        return this.configuration == null ? null : this.configuration.buildSessionFactory(); //build the sessionfactory
    }
}