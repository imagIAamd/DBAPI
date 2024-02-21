package cat.amd.dbapi.endpoints;

import cat.amd.dbapi.persistence.db.entities.Request;
import cat.amd.dbapi.persistence.db.managers.RequestManager;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;

import java.util.Map;

@Path("/request")
public class RequestController {

    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertRequest(String data) {

        boolean inserted;
        Request request;

        JSONObject requestJson = new JSONObject(data);
        System.out.println(data);
        request = new Request(requestJson);
        request = RequestManager.insertRequest(request);

        inserted = request != null;

        if (!inserted) {
            return Response.serverError().build();
        }

        RequestManager.storeRequestImages(requestJson.getJSONArray("images"), request);

        return Response.accepted().build();
    }
}
