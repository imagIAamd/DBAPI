package cat.amd.dbapi.persistence.db.managers;

import cat.amd.dbapi.persistence.db.entities.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;

import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class CommonManager {

    private CommonManager() {

    }

    /**
     * Generates an AccessKey for desired user
     *
     * @param user user to generate access key into
     * @return access key
     */
    public static String generateAccessKey(User user) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 12);

        Date currentDate = new Date();
        Date expirationDate = calendar.getTime();

        Algorithm algorithm = Algorithm.HMAC256(user.getTelephone());

        return JWT.create()
                .withIssuedAt(currentDate)
                .withExpiresAt(expirationDate)
                .sign(algorithm);
    }

    /**
     * Verifies the received access key
     *
     * @param user user that sent the key
     * @return decoded key
     * @throws JWTVerificationException if not valid throws exception
     */
    public static DecodedJWT verifyAccessKey(User user) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(user.getTelephone());
        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        return verifier.verify(user.getAccessKey());
    }

    /**
     * Returns an OK response
     *
     * @param data custom response data
     * @param status response status
     * @param message response message
     * @return built response
     */
    public static Response buildResponse(Response.Status status, JSONObject data, String message) {
        JSONObject responseBody = buildDefaultResponseBody(status.toString(), message);
        responseBody.put("data", data);

        return Response.status(status)
                .entity(responseBody.toString())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();

    }

    /**
     * Returns the default response body
     *
     * @param status response status
     * @param message response message
     * @return default response body
     */
    private static JSONObject buildDefaultResponseBody(String status, String message) {
        JSONObject responseJSON = new JSONObject();
        responseJSON.put("status", status)
                .put("MESSAGE", message);

        return responseJSON;
    }


}
