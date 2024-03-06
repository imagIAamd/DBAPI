package cat.amd.dbapi.persistence.db.managers;

import cat.amd.dbapi.persistence.db.entities.Group;
import cat.amd.dbapi.persistence.db.entities.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GroupManager {

    private GroupManager() {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupManager.class);

    /**
     * Sends a query to retrieve Role entity with given id.
     *
     * @param id role id
     * @return Role if found, null if it doesn't exist
     */
    public static Group findGroup(Long id) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Group group = null;

        try {
            tx = session.beginTransaction();
            Query<Group> query = session.createQuery("FROM User_group WHERE id = :id", Group.class);
            query.setParameter("id", id);
            group = query.uniqueResult();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("HibernateException trying to find Group with Id {}", id);
        } finally {
            session.close();
        }

        return group;
    }

    /**
     * Sends a query to retrieve Role entity with given Role entity.
     *
     * @param group role
     * @return Role if found, creates it if not found
     */
    public static Group findGroup(Group group) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        String name = group.getName();
        String description = group.getDescription();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Query<Group> query = session.createQuery("FROM User_group WHERE name = :name", Group.class);
            query.setParameter("name", group.getName());
            group = query.uniqueResult();

            if (group == null) {
                group = new Group();
                group.setName(name);
                group.setDescription(description);
                session.merge(group);
                tx.commit();
            }

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("HibernateException trying to find Group {}", group);
        } finally {
            session.close();
        }

        return group;
    }

    public static Group findGroup(String name) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Group group = null;

        try {
            tx = session.beginTransaction();
            Query<Group> query = session.createQuery("FROM User_group WHERE name = :name", Group.class);
            query.setParameter("name", name);
            group = query.uniqueResult();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("HibernateException trying to find Group {}", name);
        } finally {
            session.close();
        }

        return group;
    }


    public static List<Group> getUserRoles(Long userId) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        List<Group> groups = new ArrayList<>();
        try  {
            tx = session.beginTransaction();
            User user = UserManager.findUser(userId);
            if (user == null) {
                return groups;
            }

            Query<Group> query = session.createQuery("SELECT r.id, r.name, r.description FROM User u JOIN u.groups r WHERE u.id = :id", Group.class);
            query.setParameter("id", user.getId());
            groups = query.list();
            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("HibernateException trying to find retrieve user groups", e);
        } finally {
            session.close();
        }

        return groups;
    }

    public static boolean addUserGroup(User user, String groupName) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        List<Group> groups = new ArrayList<>();
        try  {
            tx = session.beginTransaction();

            Group group = findGroup(groupName);
            if (group == null) {
                return false;
            }

            if (!getUserRoles(user.getId()).contains(group)) {
                user.getRoles().add(group);
            }

            user.getRoles().add(group);
            session.merge(user);
            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("HibernateException trying to find retrieve user groups", e);
            return false;

        } finally {
            session.close();
        }

        return true;
    }

    public static boolean removeUserGroup(User user, String groupName) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        List<Group> groups = new ArrayList<>();
        try  {
            tx = session.beginTransaction();

            Group group = findGroup(groupName);
            if (group == null) {
                return false;
            }

            Set<Group> tmpGroups = user.getRoles();
            user.setRoles(new HashSet<>());
            for (Group dummyGroup : tmpGroups) {
                if (!Objects.equals(group.getName(), dummyGroup.getName())) {
                    user.getRoles().add(dummyGroup);
                }
            }

            session.merge(user);
            tx.commit();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("HibernateException trying to find retrieve user groups", e);
            return false;

        } finally {
            session.close();
        }

        return true;
    }




}
