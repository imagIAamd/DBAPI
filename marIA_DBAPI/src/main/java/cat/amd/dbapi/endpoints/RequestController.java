package cat.amd.dbapi.endpoints;

import cat.amd.dbapi.persistence.db.entities.Request;
import cat.amd.dbapi.persistence.db.entities.User;
import cat.amd.dbapi.persistence.db.managers.CommonManager;
import cat.amd.dbapi.persistence.db.managers.RequestManager;
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

@Path("/request")
public class RequestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);
    private static final String[] REQUEST_INSERT__REQUEST_TEMPLATE = new String[]{
            PROMPT,
            MODEL,
            IMAGES
    };

    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertRequest(@HeaderParam(value = "Authorization") String authorization, String data) {
        JSONObject responseData = new JSONObject();
        JSONObject requestJson = new JSONObject(data);

        LOGGER.info("Received insert image request");

        if (!CommonManager.isValidAuthorization(authorization)) {
            return CommonManager.buildBadRequestResponse();
        }

        if (!CommonManager.isValidAuthorization(authorization)) {
            return CommonManager.buildBadRequestResponse();
        }

        if (!CommonManager.isValidRequest(requestJson, REQUEST_INSERT__REQUEST_TEMPLATE)) {
            return CommonManager.buildBadRequestResponse();
        }

        String[] splitAuthorization = authorization.split(" ");
        DecodedJWT decodedJWT = CommonManager.verifyAccessKey(splitAuthorization[1]);
        User user = UserManager.findUser(decodedJWT.getClaim("userId").asLong());

        Request request = new Request(requestJson);
        request.setUser(user);
        request = RequestManager.insertRequest(request);
        boolean inserted = request != null;

        if (!inserted) {
            return CommonManager.buildBadRequestResponse();
        }
        RequestManager.storeRequestImages(requestJson.getJSONArray("images"), request);
        LOGGER.info("request successfully inserted");
        responseData.put("id", request.getId());
        return CommonManager.buildOkResponse(responseData, REQUEST_INSERT_OK);
    }
}
