package cat.amd.dbapi.endpoints;

import cat.amd.dbapi.persistence.db.entities.APIResponse;
import cat.amd.dbapi.persistence.db.managers.CommonManager;
import cat.amd.dbapi.persistence.db.managers.ResponseManager;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/response")
public class ResponseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseController.class);

    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertResponse(@HeaderParam(value = "Authorization")String authorization, String data) {
        APIResponse response;
        JSONObject responseData = new JSONObject();

        LOGGER.info("Received insert response request");

        if (CommonManager.isValidAuthorization(authorization)) {
            return CommonManager.buildResponse(
                    Response.Status.BAD_REQUEST,
                    responseData,
                    "bad request");
        }

        String[] splitAuthorization = authorization.split(" ");
        try {
            DecodedJWT decodedKey = CommonManager.verifyAccessKey(splitAuthorization[1]);

        } catch (JWTVerificationException e) {
            LOGGER.error("ERROR trying to verify received access_key");
            return CommonManager.buildResponse(
                    Response.Status.UNAUTHORIZED,
                    responseData,
                    "unauthorized");
        }

        JSONObject requestJson = new JSONObject(data);
        response = new APIResponse(requestJson);

        try {
            response = ResponseManager.findResponse(response);
        } catch (Exception e) {
            LOGGER.error("An error happened while processing request", e);
            return CommonManager.buildResponse(
                    Response.Status.BAD_REQUEST,
                    responseData,
                    "ERROR");
        }

        responseData.put("id", response.getId())
                .put("request_id", response.getRequest().getId());
        return CommonManager.buildResponse(
                Response.Status.OK,
                responseData,
                "response successfully registered"
        );
    }
}
