package cat.amd.dbapi.persistence.db.managers;

import cat.amd.dbapi.persistence.db.entities.Role;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleManager {

    private RoleManager() {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleManager.class);

    /**
     * Sends a query to retrieve Role entity with given id.
     *
     * @param id role id
     * @return Role if found, null if it doesn't exist
     */
    public static Role findRole(Long id) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Role role = null;

        try {
            tx = session.beginTransaction();
            Query<Role> query = session.createQuery("FROM Role WHERE id = :id", Role.class);
            query.setParameter("id", id);
            role = query.uniqueResult();

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("HibernateException trying to find role with Id {}", id);
        } finally {
            session.close();
        }

        return role;
    }

    /**
     * Sends a query to retrieve Role entity with given Role entity.
     *
     * @param role role
     * @return Role if found, creates it if not found
     */
    public static Role findRole(Role role) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        String name = role.getName();
        String description = role.getDescription();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Query<Role> query = session.createQuery("FROM Role WHERE name = :name", Role.class);
            query.setParameter("name", role.getName());
            role = query.uniqueResult();

            if (role == null) {
                role = new Role();
                role.setName(name);
                role.setDescription(description);
                session.merge(role);
                tx.commit();
            }

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("HibernateException trying to find role {}", role);
        } finally {
            session.close();
        }

        return role;
    }

}
