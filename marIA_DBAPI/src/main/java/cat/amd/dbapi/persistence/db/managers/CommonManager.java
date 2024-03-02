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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static cat.amd.dbapi.Constants.BAD_REQUEST;
import static cat.amd.dbapi.Constants.SECRET_KEY;

public class CommonManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonManager.class);

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

        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

        return JWT.create()
                .withClaim("userId", user.getId())
                .withIssuedAt(currentDate)
                .withExpiresAt(expirationDate)
                .sign(algorithm);
    }

    /**
     * Verifies the received access key
     *
     * @param accessKey received access_key
     * @return decoded key
     * @throws JWTVerificationException if not valid throws exception
     */
    public static DecodedJWT verifyAccessKey(String accessKey) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm)
                .build();

        return verifier.verify(accessKey);
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

        LOGGER.info("Returning the following response: {}", responseBody);

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
                .put("message", message);

        return responseJSON;
    }

    /**
     * Returns a bad request response
     *
     * @return response
     */
    public static Response buildBadRequestResponse() {
        JSONObject data = new JSONObject();
        return buildResponse(
                Response.Status.BAD_REQUEST,
                data,
                BAD_REQUEST
        );
    }

    /**
     * Returns an unauthorized response
     *
     * @return response
     */
    public static Response buildUnauthorizedResponse() {
        JSONObject data = new JSONObject();
        return buildResponse(
                Response.Status.UNAUTHORIZED,
                data,
                BAD_REQUEST
        );
    }

    /**
     * Returns an OK response with custom data and message
     *
     * @return response
     */
    public static Response buildOkResponse(JSONObject data, String message) {
        return buildResponse(
                Response.Status.OK,
                data,
                message
        );
    }

    /**
     * Returns true if the given authorization string is valid
     *
     * @param authorization authorization string
     * @return true if valid
     */
    public static boolean isValidAuthorization(String authorization) {
        if (authorization == null || authorization.trim().isEmpty()) {
            return false;
        }

        String[] splitAuthorization = authorization.split(" ");
        int splitLength = splitAuthorization.length;

        if (splitLength != 2 || Objects.equals(splitAuthorization[0], "Bearer")) {
            return false;
        }

        try {
            CommonManager.verifyAccessKey(splitAuthorization[1]);
        } catch (JWTVerificationException e) {
            return false;
        }

        return true;
    }

    /**
     * Returns true if the given JSONObject request contains the given template parameters
     *
     * @param request request to validate
     * @param template template to validate from
     * @return true if valid
     */
    public static boolean isValidRequest(JSONObject request, String[] template) {
        for (String key : template) {
            if (!request.has(key)) return false;
        }
        return true;
    }


}
