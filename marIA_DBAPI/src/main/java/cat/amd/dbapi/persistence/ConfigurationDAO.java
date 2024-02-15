package cat.amd.dbapi.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.HibernateException;

public class ConfigurationDAO {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationDAO.class);

    /**
     * Tries to find an existing configuration by the 'name' String parameter.
     * If not found creates it.
     *
     * @param name configuration name
     * @return found or created configuration
     */
    public static  Configuration findConfigurationByName(String name) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Configuration configuration = null;

        try {
            tx = session.beginTransaction();
            Query<Configuration> query = session.createQuery("FROM Configuration WHERE name = :name", Configuration.class);
            query.setParameter("name", name);
            configuration = query.uniqueResult();

            if (configuration == null) {
                configuration = new Configuration(name);
                session.save(configuration);
                tx.commit();
                logger.info("New configuration created by the name '{}'", name);
            } else {
                logger.info("Found configuration by the name: {}", name);
            }

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();;
            logger.error("Error trying to find configuration by the name '{}'", name);
        } finally {
            session.close();
        }

        return configuration;
    }

    /**
     * Tries to add a 'newProperty' Property parameter to 'configuration' Configuration parameter
     *
     * @param newProperty property to add
     * @param configuration destiny configuration
     */
    public static void addPropertyToConfiguration(Property newProperty, Configuration configuration) {

        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            boolean found = false;

            for (Property existingProperty : configuration.getProperties()) {
                if (!existingProperty.getKey().equals(newProperty.getKey())) {
                    continue;
                }
                existingProperty.setValue(newProperty.getKey());
                session.update(existingProperty);
                found = true;
                break;
            }
            if (found) {
                newProperty.setConfiguration(configuration);
                configuration.addProperty(newProperty);
                session.save(newProperty);
                logger.info("Successfully added new property to {} configuration", configuration.getName());
            }
            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error trying to link or update property to the configuration", e);
        }
    }

    /**
     * Tries to delete Property with key 'key' from the 'configuration' Configuration
     *
     * @param key property key
     * @param configuration configuration to delete from
     * @return true if deleted, false if not found or not deleted
     */
    public static boolean deletePropertyFromConfiguration(String key, Configuration configuration) {
        Transaction tx = null;
        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Property propertyToDelete = null;
            for (Property property : configuration.getProperties()) {
                if (property.getKey().equals(key)) {
                    propertyToDelete = property;
                    break;
                }
            }
            if (propertyToDelete == null) {
                return false;
            }

            session.remove(propertyToDelete);
            tx.commit();
            configuration.getProperties().remove(propertyToDelete);
            logger.info("Property {} successfully deleted from {} configuration", key, configuration.getName());
            return true;

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error trying to delete the property from the configuration");
            return  false;
        }
    }

}
