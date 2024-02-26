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

@Path("/request")
public class RequestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestController.class);

    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertRequest(@HeaderParam(value = "Authorization")String authorization, String data) {
        Request request;
        JSONObject responseData = new JSONObject();

        LOGGER.info("Received insert image request");

        if (CommonManager.isValidAuthorization(authorization)) {
            return CommonManager.buildResponse(
                    Response.Status.BAD_REQUEST,
                    responseData,
                    "bad request");
        }

        String[] splitAuthorization = authorization.split(" ");
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
        JSONObject requestJson = new JSONObject(data);

        request = new Request(requestJson);
        request.setUser(user);
        request = RequestManager.insertRequest(request);
        boolean inserted = request != null;

        if (!inserted) {
            return Response.serverError().build();
        }

        RequestManager.storeRequestImages(requestJson.getJSONArray("images"), request);

        return Response.accepted().build();
    }
}
