package cat.amd.dbapi.endpoints;

import cat.amd.dbapi.persistence.db.entities.User;
import cat.amd.dbapi.persistence.db.managers.CommonManager;
import cat.amd.dbapi.persistence.db.managers.GroupManager;
import cat.amd.dbapi.persistence.db.managers.UserManager;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mysql.cj.util.StringUtils;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cat.amd.dbapi.Constants.*;


@Path("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private static final String[] USER_REGISTER_REQUEST_TEMPLATE = new String[]{
            PHONE_NUMBER,
            NICKNAME,
            EMAIL,
            VALIDATION_CODE
    };
    private static final String[] USER_VALIDATE_REQUEST_TEMPLATE = new String[]{
            PHONE_NUMBER,
            VALIDATION_CODE
    };
    private static final String[] USER_LOGIN_TEMPLATE = new String[]{
            EMAIL,
            PASSWORD
    };
    private static final String[] ADMIN_CHANGE_PLAN_TEMPLATE = new String[]{
            PHONE_NUMBER,
            PLAN
    };

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

        if (!CommonManager.isValidRequest(requestJson, USER_REGISTER_REQUEST_TEMPLATE)) {
            return CommonManager.buildBadRequestResponse();
        }

        user = new User(requestJson);
        LOGGER.info("Received SMS code: {}", requestJson.get(VALIDATION_CODE));
        if (UserManager.findUser(user) == null) {
            return CommonManager.buildBadRequestResponse();
        }

        responseData.put(NICKNAME, user.getNickname())
                .put(PHONE_NUMBER, user.getTelephone())
                .put(EMAIL, user.getEmail());

        LOGGER.info("User successfully registered");
        return CommonManager.buildOkResponse(responseData, USER_REGISTER_OK);
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

        if (!CommonManager.isValidRequest(requestJson, USER_VALIDATE_REQUEST_TEMPLATE)) {
            LOGGER.warn("Received request is not valid");
            return  CommonManager.buildBadRequestResponse();
        }
        User user = UserManager.findUserByTelephone(requestJson.getString(PHONE_NUMBER));
        if (user == null) {
            LOGGER.warn("User was not found");
            return CommonManager.buildBadRequestResponse();
        }
        if (user.getValidationCode() != requestJson.getInt(VALIDATION_CODE)) {
            LOGGER.warn("The validation code doesn't match");
            return CommonManager.buildBadRequestResponse();
        }

        String accessKey = CommonManager.generateAccessKey(user);
        UserManager.updateUser(user);
        responseData.put(ACCESS_KEY, accessKey)
                .put(NICKNAME, user.getNickname())
                .put(PHONE_NUMBER, user.getTelephone())
                .put(EMAIL, user.getEmail());

        LOGGER.info("User validated");
        return CommonManager.buildOkResponse(responseData, USER_VALIDATION_OK);
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

    @POST
    @Path("/login")
    public Response userLogin(String data) {
        JSONObject requestJson = new JSONObject(data);
        JSONObject responseData = new JSONObject();

        if (!CommonManager.isValidRequest(requestJson, USER_LOGIN_TEMPLATE)) {
            return CommonManager.buildBadRequestResponse();
        }
        String email = requestJson.getString(EMAIL);
        String password = requestJson.getString(PASSWORD);
        User user = UserManager.findUser(email, password);
        if (user == null) {
            return CommonManager.buildUnauthorizedResponse();
        }

        String accessKey = CommonManager.generateAccessKey(user);
        responseData.put("access_key", accessKey);
        return CommonManager.buildOkResponse(responseData, "Congratulations!");
    }

    @GET
    @Path("/admin_get_list")
    public Response getList(@HeaderParam(value = "Authorization") String authorization,
                            @QueryParam("nickname") String nickname, @QueryParam("limit") String limit) {

        LOGGER.info("Received new request in /admin_get_list");
        if (!CommonManager.isValidAuthorization(authorization)) {
            return CommonManager.buildUnauthorizedResponse();
        }

        List<User> users = new ArrayList<>();
        if (nickname == null && limit == null) {
            users = UserManager.findUsers();
        } else if (limit == null) {
            users = UserManager.findUsers(nickname);
        } else {
            if (StringUtils.isStrictlyNumeric(limit)) {
                UserManager.findUsers(Integer.parseInt(limit));
            } else {
                users = UserManager.findUsers();
            }
        }

        JSONArray usersData = new JSONArray();
        for (User user : users) {
            usersData.put(user.toJSON(GroupManager.getUserRoles(user.getId())));
        }

        return CommonManager.buildOkResponse(usersData, "OK");
    }

    @POST
    @Path("admin_change_plan")
    public Response changePlan(@HeaderParam(value = "Authorization") String authorization, String data) {

        LOGGER.info("Received new request in /admin_change_plan");
        JSONObject requestJson = new JSONObject(data);

        if (!CommonManager.isValidRequest(requestJson, ADMIN_CHANGE_PLAN_TEMPLATE)) {
            return CommonManager.buildBadRequestResponse();
        }

        if (!CommonManager.isValidAuthorization(authorization)) {
            return CommonManager.buildUnauthorizedResponse();
        }

        User user = UserManager.findUserByTelephone(requestJson.getString(PHONE_NUMBER));
        if (user == null) {
            return CommonManager.buildBadRequestResponse();
        }

        String plan = requestJson.getString("plan");
        boolean updated = false;
        if (Objects.equals(plan, PREMIUM)) {
            updated = GroupManager.addUserGroup(user, ROLE_PREMIUM_NAME);
        } else if (Objects.equals(plan, FREE)) {
            updated = GroupManager.removeUserGroup(user, ROLE_PREMIUM_NAME);
        }

        if (!updated) {
            LOGGER.warn("Unable to update the requested user groups");
            return CommonManager.buildBadRequestResponse();
        }

        JSONObject responseData = new JSONObject();
        responseData.put("plan", plan);
        return CommonManager.buildOkResponse(responseData, "Congratulations!");

    }
}
