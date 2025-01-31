package eu.highgeek.highgeeksync.data.sql.daos;

import eu.highgeek.highgeeksync.HighgeekSync;

import eu.highgeek.highgeeksync.data.sql.DatabaseFactory;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseDao<T> {

    private Session session;
    private final Logger logger;
    private final HighgeekSync highgeekSync;

    public BaseDao(
            final @NotNull HighgeekSync aoHibernate
    ) {
        this.highgeekSync = aoHibernate;
        this.logger = this.highgeekSync.getLogger();
    }

    /**
     * Persists the given entity into the database.
     *
     * @param data the entity to be persisted
     */
    public void persistEntity(
            final T data
    ) {
        try (final Session session = this.getOrCreateSession()) {
            session.beginTransaction();
            session.persist(data);
            session.flush();
            session.getTransaction().commit();
        } catch (
                final Exception exception
        ) {
            this.logger.log(
                    Level.SEVERE,
                    "Failed to persist entity",
                    exception
            );
        } finally {
            session.close();
        }
    }

    /**
     * Get or create a session.
     *
     * @return the session object
     */
    public Session getOrCreateSession() {
        final SessionFactory sessionFactory = this.getSessionFactory();
        if (sessionFactory == null)
            return null;

        if (this.session == null)
            this.session = sessionFactory.withOptions().flushMode(FlushMode.AUTO).openSession();

        return !this.session.isOpen() ? this.getSessionFactory().withOptions().flushMode(FlushMode.AUTO).openSession() : this.session;
    }

    public @Nullable Session getSession() {
        return this.session;
    }

    /**
     * Removes the specified entity from the database.
     *
     * @param data the entity to be removed
     */
    public void removeEntity(
            final T data
    ) {
        try (final Session session = this.getOrCreateSession()) {
            session.beginTransaction();
            session.remove(data);
            session.getTransaction().commit();
        } catch (final Exception exception) {
            if (
                    session.getTransaction() != null &&
                            session.getTransaction().isActive()
            ) session.getTransaction().rollback();
            this.logger.log(
                    Level.SEVERE,
                    "Failed to remove entity",
                    exception
            );
        } finally {
            session.close();
        }
    }

    /**
     * Creates and returns a named query of type Query based on the provided query name.
     *
     * @param queryName the name of the query
     *
     * @return the created named query
     */
    protected Query<T> createNamedQuery(
            final @NotNull String queryName
    ) {
        return this.getOrCreateSession().createNamedQuery(
                queryName,
                this.getClazzType()
        );
    }

    /**
     * Retrieves the class type of the objects managed by this function.
     *
     * @return The class type of the objects.
     */
    protected abstract Class<T> getClazzType();

    /**
     * Retrieves the session factory.
     *
     * @return         	the session factory, or null if it cannot be retrieved
     */
    private @Nullable SessionFactory getSessionFactory() {
        final DatabaseFactory databaseFactory = new DatabaseFactory(this.highgeekSync);
        return databaseFactory.buildSessionFactory();
    }
}