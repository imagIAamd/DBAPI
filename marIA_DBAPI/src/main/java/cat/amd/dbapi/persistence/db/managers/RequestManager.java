package cat.amd.dbapi.persistence.db.managers;

import cat.amd.dbapi.persistence.db.entities.Request;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;

public class RequestManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(cat.amd.dbapi.persistence.db.managers.RequestManager.class);

    /**
     * Tries to find an existing user with 'nickname' String parameter.
     * If not found creates it.
     *
     * @param request request to insert
     * @return Request
     */
    public static Request insertRequest(Request request) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.save(request);
            tx.commit();
            LOGGER.info("New request inserted from user '{}'", request.getUser().getNickname());
            return request;

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            ;
            LOGGER.error("Error trying to insert request from user  '{}'", request.getUser().getNickname(), e);
        } finally {
            session.close();
        }

        return null;
    }

    public static boolean updateRequest(Request request) {
        Session session = SessionFactoryManager.getSessionFactory().openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            session.merge(request);
            tx.commit();
            LOGGER.info("Updated image_path colum from request '{}'", request.getId());
            return true;

        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            LOGGER.error("Error trying to update image_path column from request '{}'", request.getId());

        } finally {
            session.close();
        }

        return false;
    }

    /**
     *
     * @param images JSONArray of images to store
     * @param request Request entity
     */
    public static void storeRequestImages(JSONArray images, Request request) {
        String outputFilePath = "data/images/NAME/IMAGE.jpeg";

        for (int i = 0; i<images.length(); i++) {
            JSONObject image = images.getJSONObject(i);
            String base64 = image.getString("image");
            byte[] imageBytes = Base64.getDecoder().decode(base64);

            String outputPath = outputFilePath
                    .replace("NAME", "request_" + request.getId())
                    .replace("IMAGE", "image_" + base64.substring(0, 10));
            Path imagePath = Path.of(outputPath);

            try {
                Files.createDirectories(imagePath.getParent());
                Files.write(imagePath, imageBytes, StandardOpenOption.CREATE_NEW);
                request.setImagePath(outputPath);
                updateRequest(request);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }


}
