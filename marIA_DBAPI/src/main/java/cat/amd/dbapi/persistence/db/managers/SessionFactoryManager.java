package cat.amd.dbapi.persistence.db.managers;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionFactoryManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionFactoryManager.class);
    private static final SessionFactory factory;

    static {
        try {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure()
                    .build();

            Metadata metadata = new MetadataSources(registry).buildMetadata();
            factory = metadata.getSessionFactoryBuilder().build();
        } catch (Throwable e) {
            logger.error("Error on sessionFactory object creation.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * SessionFactory getter
     *
     * @return returns the current instance of SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        return factory;
    }

    /**
     * If the current SessionFactory instance is not null, closes it.
     *
     */
    public static void close() {
        if (factory != null) {
            factory.close();
        }
    }
}
