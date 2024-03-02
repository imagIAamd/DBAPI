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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.*;

import static cat.amd.dbapi.Constants.*;

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
        Date currentDate = new Date();
        Date expirationDate = calculateExpirationDate();
        String secretKey = generateSecretKey();

        storeSecretKey(user, secretKey);
        System.out.println(secretKey.toString());
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return JWT.create()
                .withClaim("userId", user.getId())
                .withIssuedAt(currentDate)
                .withExpiresAt(expirationDate)
                .sign(algorithm);
    }

    /**
     * Generates a secret key
     *
     * @return generated secret key
     */
    private static String generateSecretKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] secretBytes = new byte[SECRET_KEY_LENGTH];
        secureRandom.nextBytes(secretBytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(secretBytes);
    }

    /**
     * Encode the given data to Base64
     *
     * @param data data to encode
     * @return encoded data
     */
    private static String encodeToBase64(String data) {
        try {
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            LOGGER.error("Error encoding to base64: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Decodes the given data from Base64
     *
     * @param input data to decode
     * @return decoded data
     */
    private static String decodeFromBase64(String input) {
        try {
            StringBuilder trimmedInput = new StringBuilder(input.trim());

            while (trimmedInput.length() % 4 != 0) {
                trimmedInput.append("=");
            }

            byte[] decodedBytes = Base64.getDecoder().decode(trimmedInput.toString());
            return new String(decodedBytes);

        } catch (IllegalArgumentException e) {
            LOGGER.error("Error decoding base64 string: {}", e.getMessage());
            return null;
        }
    }

    private static String decodeFromBase64URL(String input) {
        return new String(Base64.getUrlDecoder().decode(input));
    }

    /**
     * Stores the user related secret key into a file
     *
     * @param user user related to the key
     * @param key key to store
     */
    private static void storeSecretKey(User user, String key) {
        String outputFilePath = SECRET_OUTPUT_PATH.replace("USER", user.getId().toString());
        String encodedKey = encodeToBase64(key);

        Path keyPath = Path.of(outputFilePath);

        try {
            Files.createDirectories(keyPath.getParent());
            Files.write(keyPath, encodedKey.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            LOGGER.info("Secret key for user {} stored at: {}", user.getId(), keyPath.toAbsolutePath());
        } catch (IOException e) {
            LOGGER.error("Error storing secret key for user " + user.getId(), e);
        }
    }

    /**
     * Retrieves the given user secret key from a file
     *
     * @param user user to get the key of
     * @return retrieved key
     */
    public static String retrieveSecretKey(User user) {
        String inputFilePath = SECRET_OUTPUT_PATH.replace("USER", user.getId().toString());
        Path keyPath = Path.of(inputFilePath);

        try {
            byte[] encodedKeyBytes = Files.readAllBytes(keyPath);
            String encodedKey = new String(encodedKeyBytes);

            return decodeFromBase64URL(encodedKey);
        } catch (IOException e) {
            LOGGER.error("Error retrieving secret key for user " + user.getId(), e);
            return null;
        }
    }

    /**
     * Verifies the received access key
     *
     * @param accessKey received access_key
     * @return decoded key
     * @throws JWTVerificationException if not valid throws exception
     */
    public static DecodedJWT verifyAccessKey(String accessKey) throws JWTVerificationException {
        DecodedJWT decodedJWT = JWT.decode(accessKey);
        User user = UserManager.findUser(decodedJWT.getClaim("userId").asLong());
        String secretKey = retrieveSecretKey(user);

        if (secretKey == null) {
            LOGGER.info("No secret key for user with id {}", user.getId());
            throw new JWTVerificationException("");
        }

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
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

        if (splitLength != 2 || !Objects.equals(splitAuthorization[0], "Bearer")) {
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

    /**
     * Returns the calculated expiration date
     *
     * @return expiration date
     */
    private static Date calculateExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 12);
        return calendar.getTime();
    }

}
