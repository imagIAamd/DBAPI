package cat.amd.dbapi.persistence.db.managers;

import cat.amd.dbapi.persistence.db.entities.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static cat.amd.dbapi.Constants.*;

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
            }

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("Error trying to find user with nickname '{}'", nickname, e);
        } finally {
            session.close();
        }

        return user;
    }

    /**
     * Tries to find an existing user with 'id' Long parameter.
     *
     * @param id user id
     * @return User if found, null if else
     */
    public static User findUser(Long id) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        User user = null;

        try {
            tx = session.beginTransaction();
            Query<User> query = session.createQuery("FROM User WHERE id = :id", User.class);
            query.setParameter("id", id);
            user = query.uniqueResult();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            ;
            LOGGER.error("Error trying to find user with id '{}'", id, e);
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
        final String telephone = user.getTelephone();
        Transaction tx = null;
        User foundUser;

        try {
            tx = session.beginTransaction();
            Query<User> query = session.createQuery("FROM User WHERE telephone = :telephone", User.class);
            query.setParameter("telephone", telephone);
            foundUser = query.uniqueResult();

            if (foundUser == null) {
                session.merge(user);
                tx.commit();
            }

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("Error finding user", e);
            return null;

        } finally {
            session.close();
        }

        return user;
    }

    /**
     * Finds a user by its registered phone number for validation purposes
     *
     * @param telephone received telephone
     * @return found user
     */
    public static User findUserByTelephone(String telephone) {
        Transaction tx = null;
        User user = null;

        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Query<User> query = session.createQuery("FROM User WHERE telephone = :telephone", User.class);
            query.setParameter("telephone", telephone);
            user = query.uniqueResult();
            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("Error finding user", e);
        }

        return user;
    }

    /**
     * Checks if a user exists
     *
     * @param user user to check
     * @return true if exists, else false
     */
    public static boolean userExists(User user) {
        Transaction tx = null;
        boolean exists = false;

        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            User foundUser;
            tx = session.beginTransaction();
            Query<User> query = session.createQuery("FROM User WHERE telephone = :telephone", User.class);
            query.setParameter("telephone", user.getTelephone());
            foundUser = query.uniqueResult();

            if (foundUser != null) {
                exists = true;
            }

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();

        }

        return exists;
    }

    /**
     * Updates an existing user
     *
     * @param user user to update
     */
    public static void updateUser(User user) {
        Transaction tx = null;

        try (Session session = SessionFactoryManager.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(user);
            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("Error updating user", e);

        }
    }

    public static User findUser(String email, String password) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        User user = null;

        try {
            tx = session.beginTransaction();
            Query<User> query = session.createQuery("FROM User " +
                    "WHERE email = :email AND password = :password", User.class);
            query.setParameter(EMAIL, email);
            query.setParameter(PASSWORD, password);
            user = query.uniqueResult();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("Error trying to find user with email '{}'", email);
        } finally {
            session.close();
        }

        return user;
    }

    public static List<User> findUsers() {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        List<User> users = new ArrayList<>();

        try {
            tx = session.beginTransaction();
            Query<User> query = session.createQuery("FROM User", User.class);
            users = query.list();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("Error trying to find all users");
        } finally {
            session.close();
        }

        return users;
    }

    public static List<User> findUsers(String nickname) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        List<User> users = new ArrayList<>();

        try {
            tx = session.beginTransaction();
            Query<User> query = session.createQuery("FROM User WHERE nickname := nickname", User.class);
            query.setParameter("nickname", nickname);
            users = query.list();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("Error trying to find all users");
        } finally {
            session.close();
        }

        return users;
    }

    public static List<User> findUsers(int limit) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        List<User> users = new ArrayList<>();

        try {
            tx = session.beginTransaction();
            Query<User> query = session.createQuery("FROM User LIMIT :limit", User.class);
            query.setParameter("limit", limit);
            users = query.list();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("Error trying to find all users");
        } finally {
            session.close();
        }

        return users;
    }

    public static List<User> findUsers(String nickname, int limit) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        List<User> users = new ArrayList<>();

        try {
            tx = session.beginTransaction();
            Query<User> query = session
                    .createQuery("FROM User WHERE nickname = :nickname LIMIT :limit", User.class);
            query.setParameter("nickname", nickname);
            query.setParameter("limit", limit);
            users = query.list();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("Error trying to find all users");
        } finally {
            session.close();
        }

        return users;
    }

}
