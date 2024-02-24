package cat.amd.dbapi.persistence.db.managers;

import cat.amd.dbapi.persistence.db.entities.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserManager {

    private UserManager() {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UserManager.class);

    /**
     * Tries to find an existing user with 'nickname' String parameter.
     * If not found creates it.
     *
     * @param nickname user nickname
     * @return found or created user
     */
    public static User findUser(String nickname) {
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
                session.merge(user);
                tx.commit();
                LOGGER.info("New User created with nickname '{}'", nickname);
            } else {
                LOGGER.info("Found user with nickname: {}", nickname);
            }

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();;
            LOGGER.error("Error trying to find user with nickname '{}'", nickname, e);
        } finally {
            session.close();
        }

        return user;
    }

    /**
     * Tries to find an existing user.
     * If not found creates it.
     *
     * @param user user to find
     * @return user found
     */
    public static User findUser(User user) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        User foundUser;

        try {
            tx = session.beginTransaction();
            Query<User> query = session.createQuery("FROM User WHERE telephone = :telephone", User.class);
            query.setParameter("telephone", user.getTelephone());
            foundUser = query.uniqueResult();
            LOGGER.info("The phone number '{}' is already registered", user.getTelephone());

            if (foundUser == null) {
                session.merge(user);
                tx.commit();
                LOGGER.info("New User created with nickname '{}'", user.getNickname());

            }

        } catch (HibernateException e) {
             if (tx != null) tx.rollback();
             LOGGER.error("Error finding user", e);

        } finally {
            session.close();
        }

        return user;
    }

}
