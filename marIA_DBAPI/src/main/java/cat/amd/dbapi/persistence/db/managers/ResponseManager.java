package cat.amd.dbapi.persistence.db.managers;

import cat.amd.dbapi.persistence.db.entities.APIResponse;
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
     * @param apiResponse received response
     * @return found or created response
     */
    public static APIResponse findResponse(APIResponse apiResponse) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;
        APIResponse foundAPIResponse = null;

        try {
            tx = session.beginTransaction();
            Query<APIResponse> query = session.createQuery("FROM APIResponse WHERE request = :request", APIResponse.class);
            query.setParameter("request", apiResponse.getRequest());
            foundAPIResponse = query.uniqueResult();

            if (foundAPIResponse == null) {
                session.merge(apiResponse);
                foundAPIResponse = apiResponse;
                tx.commit();
            }

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("Error trying to response with request_id '{}'", apiResponse.getRequest().getId(), e);
        } finally {
            session.close();
        }

        return foundAPIResponse;
    }
}
