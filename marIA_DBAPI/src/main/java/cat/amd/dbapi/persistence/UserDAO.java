package cat.amd.dbapi.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    /**
     * Tries to find an existing user with 'nickname' String parameter.
     * If not found creates it.
     *
     * @param nickname configuration nickname
     * @return found or created user
     */
    public static  User findUserByNickname(String nickname) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        User user = null;

        try {
            tx = session.beginTransaction();
            Query<User> query = session.createQuery("FROM User WHERE nickname = :nickname", User.class);
            query.setParameter("nickname", nickname);
            user = query.uniqueResult();

            if (user == null) {
                user = new User(nickname);
                session.save(user);
                tx.commit();
                logger.info("New User created with the nickname '{}'", nickname);
            } else {
                logger.info("Found user with the nickname: {}", nickname);
            }

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();;
            logger.error("Error trying to find user with nickname '{}'", nickname);
        } finally {
            session.close();
        }

        return user;
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
