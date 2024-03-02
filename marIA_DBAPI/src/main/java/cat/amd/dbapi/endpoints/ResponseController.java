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

import static cat.amd.dbapi.Constants.*;

@Path("/response")
public class ResponseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseController.class);
    private static final String[] RESPONSE_INSERT_REQUEST_TEMPLATE = new String[] {
            REQUEST_ID,
            TEXT
    };

    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertResponse(@HeaderParam(value = "Authorization")String authorization, String data) {
        APIResponse response;
        JSONObject responseData = new JSONObject();

        LOGGER.info("Received insert response request with authorization : {}", authorization);

        if (!CommonManager.isValidAuthorization(authorization)) {
            return CommonManager.buildUnauthorizedResponse();
        }

        JSONObject requestJson = new JSONObject(data);
        response = new APIResponse(requestJson);

        if (!CommonManager.isValidRequest(requestJson, RESPONSE_INSERT_REQUEST_TEMPLATE)) {
            return CommonManager.buildBadRequestResponse();
        }

        response = ResponseManager.findResponse(response);
        LOGGER.info(RESPONSE_INSERT_OK);
        responseData.put("id", response.getId())
                .put("request_id", response.getRequest().getId());
        return CommonManager.buildOkResponse(responseData, RESPONSE_INSERT_OK);
    }
}
