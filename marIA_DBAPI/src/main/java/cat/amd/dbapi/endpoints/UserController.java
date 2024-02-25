package cat.amd.dbapi.endpoints;

import cat.amd.dbapi.persistence.db.entities.User;
import cat.amd.dbapi.persistence.db.managers.CommonManager;
import cat.amd.dbapi.persistence.db.managers.UserManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private static final String ACCESS_KEY = "access_key";
    private static final String NICKNAME = "nickname";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String EMAIL = "email";

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
        user.setAccessKey(CommonManager.generateAccessKey(user));
        UserManager.updateUser(user);


        responseData.put(ACCESS_KEY, user.getAccessKey())
                .put(NICKNAME, user.getNickname())
                .put(PHONE_NUMBER, user.getTelephone())
                .put(EMAIL, user.getEmail());

        return CommonManager.buildResponse(
                Response.Status.OK,
                responseData,
                "User successfully validated");
    }
}
