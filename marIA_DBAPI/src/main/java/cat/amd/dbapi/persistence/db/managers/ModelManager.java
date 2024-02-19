package cat.amd.dbapi.persistence.db.managers;

import cat.amd.dbapi.persistence.db.entities.Model;
import cat.amd.dbapi.persistence.db.entities.User;
import cat.amd.dbapi.persistence.reference.Configuration;
import cat.amd.dbapi.persistence.reference.Property;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelManager {

    private static final Logger logger = LoggerFactory.getLogger(ModelManager.class);

    /**
     * Tries to find an existing model with 'name' String parameter.
     * If not found creates it.
     *
     * @param name model name
     * @return found or created user
     */
    public static Model findModelByName(String name) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Model model = null;

        try {
            tx = session.beginTransaction();
            Query<Model> query = session.createQuery("FROM Model WHERE name = :name", Model.class);
            query.setParameter("name", name);
            model = query.uniqueResult();

            if (model == null) {
                model = new Model(name);
                session.save(model);
                tx.commit();
                logger.info("New Model created with the name '{}'", name);
            } else {
                logger.info("Found Model with the name: {}", name);
            }

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            logger.error("Error trying to find Model with name '{}'", name);
        } finally {
            session.close();
        }

        return model;
    }

}
