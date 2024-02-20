package cat.amd.dbapi.persistence.db.managers;

import cat.amd.dbapi.persistence.db.entities.User;
import cat.amd.dbapi.persistence.reference.Configuration;
import cat.amd.dbapi.persistence.reference.Property;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserManager {
    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);

    /**
     * Tries to find an existing user with 'nickname' String parameter.
     * If not found creates it.
     *
     * @param nickname user nickname
     * @return found or created user
     */
    public static User findUserByNickname(String nickname) {
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
            logger.error("Error trying to find user with nickname '{}'", nickname, e);
        } finally {
            session.close();
        }

        return user;
    }

}
