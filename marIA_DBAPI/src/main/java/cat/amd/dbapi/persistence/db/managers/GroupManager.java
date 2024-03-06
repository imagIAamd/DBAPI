package cat.amd.dbapi.persistence.db.managers;

import cat.amd.dbapi.persistence.db.entities.Group;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /*
    public Set<Role> getUserRoles(Long userId) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Role role = null;
        try () {
            Transaction transaction = session.beginTransaction();

            User user = session.get(User.class, userId);
            Set<Role> roles = user.getRoles(); // Access within the active session

            transaction.commit();
            return roles;
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
            return Collections.emptySet();
        }
    }

    */


}
