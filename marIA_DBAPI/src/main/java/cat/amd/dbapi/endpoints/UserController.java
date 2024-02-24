package cat.amd.dbapi.endpoints;

import cat.amd.dbapi.persistence.db.entities.User;
import cat.amd.dbapi.persistence.db.managers.UserManager;
import ch.qos.logback.classic.turbo.MarkerFilter;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Path("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(String data) {

        User user;

        LOGGER.info("Received new user registration request");
        JSONObject requestJson = new JSONObject(data);
        user = new User(requestJson);
        User foundUser = UserManager.findUser(user);

        if (Objects.equals(user.getNickname(), foundUser.getNickname())) {
            return Response.notModified(User.class.getName()).build();
        }

        return Response.accepted().build();

    }
}
