package cat.amd.dbapi.persistence.db.managers;

import cat.amd.dbapi.persistence.db.entities.Response;
import cat.amd.dbapi.persistence.db.entities.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseManager.class);

    private ResponseManager() {

    }

    /**
     * Tries to find an existing response.
     * If not found creates it.
     *
     * @param response received response
     * @return found or created response
     */
    public static Response findResponse(Response response) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        Response foundResponse = null;

        try {
            tx = session.beginTransaction();
            Query<Response> query = session.createQuery("FROM User WHERE request = :request", Response.class);
            query.setParameter("request", response.getRequest().getId());
            foundResponse = query.uniqueResult();

            if (foundResponse == null) {
                session.merge(response);
                tx.commit();
            }

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("Error trying to response with request_id '{}'", response.getRequest().getId(), e);
        } finally {
            session.close();
        }

        return foundResponse;
    }
}
