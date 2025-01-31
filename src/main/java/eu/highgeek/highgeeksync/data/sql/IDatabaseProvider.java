package eu.highgeek.highgeeksync.data.sql;

import org.hibernate.SessionFactory;
import org.jetbrains.annotations.Nullable;

public interface IDatabaseProvider {

    void connect() throws Exception;

    /**
     * Include annotated classes.
     *
     * @throws Exception
     */
    void includeAnnotatedClasses() throws Exception;

    @Nullable
    SessionFactory buildSessionFactory();
}
