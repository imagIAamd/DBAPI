package cat.amd.dbapi.endpoints;

import cat.amd.dbapi.persistence.db.entities.User;
import cat.amd.dbapi.persistence.db.managers.CommonManager;
import cat.amd.dbapi.persistence.db.managers.UserManager;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cat.amd.dbapi.Constants.*;


@Path("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    /**
     * Endpoint for user registration
     *
     * @param data JSON containing new user data
     * @return response with registration status
     */
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(String data) {
        LOGGER.info("Received new user registration request");

        User user;
        JSONObject responseData = new JSONObject();
        JSONObject requestJson = new JSONObject(data);
        user = new User(requestJson);

        if (UserManager.findUser(user) == null) {
            LOGGER.info("User already exists");

            responseData.put(NICKNAME, user.getNickname())
                    .put(PHONE_NUMBER, user.getTelephone())
                    .put(EMAIL, user.getEmail());

            return CommonManager.buildResponse(
                    Response.Status.CONFLICT,
                    responseData,
                    "User already exists");
        }

        LOGGER.info("User successfully registered");

        responseData.put(NICKNAME, user.getNickname())
                .put(PHONE_NUMBER, user.getTelephone())
                .put(EMAIL, user.getEmail());

        return CommonManager.buildResponse(
                Response.Status.OK,
                responseData,
                "User successfully registered");
    }

    /**
     * Endpoint for user validation
     *
     * @param data request body
     * @return server response
     */
    @POST
    @Path("/validate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateUser(String data) {
        LOGGER.info("Received new user validation request");

        JSONObject responseData = new JSONObject();
        JSONObject requestJson = new JSONObject(data);
        User user = UserManager.findUserByTelephone(requestJson.getString(PHONE_NUMBER));

        if (user == null) {
            return CommonManager.buildResponse(
                    Response.Status.BAD_REQUEST,
                    responseData,
                    "the phone number is not registered");
        }

        LOGGER.info("User validated");
        String accessKey = CommonManager.generateAccessKey(user);
        UserManager.updateUser(user);


        responseData.put(ACCESS_KEY, accessKey)
                .put(NICKNAME, user.getNickname())
                .put(PHONE_NUMBER, user.getTelephone())
                .put(EMAIL, user.getEmail());

        return CommonManager.buildResponse(
                Response.Status.OK,
                responseData,
                "User successfully validated");
    }

    /**
     * Returns the sender info
     *
     * @param authorization access_token with user data
     * @return status code
     */
    @GET
    @Path("/info")
    public Response getUserInfo(@HeaderParam(value = "Authorization") String authorization) {
        JSONObject responseData = new JSONObject();
        String[] splitAuthorization = authorization.split(" ");

        if (CommonManager.isValidAuthorization(authorization)) {
            return CommonManager.buildResponse(
                    Response.Status.BAD_REQUEST,
                    responseData,
                    "bad request");
        }

        DecodedJWT decodedKey;
        try {
            decodedKey = CommonManager.verifyAccessKey(splitAuthorization[1]);

        } catch (JWTVerificationException e) {
            LOGGER.error("ERROR trying to verify received access_key");
            return CommonManager.buildResponse(
                    Response.Status.UNAUTHORIZED,
                    responseData,
                    "unauthorized");
        }

        Long userId = decodedKey.getClaim("userId").asLong();
        User user = UserManager.findUser(userId);

        if (user != null) {
            boolean validated = user.getValidationCode() != null;
            responseData.put("nickname", user.getNickname())
                    .put("email", user.getEmail())
                    .put("phone_number", user.getTelephone())
                    .put("validated", validated)
                    .put("tos", "WIP")
                    .put("plan", "WIP");
            return CommonManager.buildResponse(
                    Response.Status.OK,
                    responseData,
                    "User information successfully retrieved");
        }

        return CommonManager.buildResponse(
                Response.Status.BAD_REQUEST,
                responseData,
                "bad request");
    }
}
