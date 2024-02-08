package cat.amd.dbapi;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class AppMain {
    private static final Logger logger = LoggerFactory.getLogger(AppMain.class);
    private static String baseURI;

    /**
     * Sets up and runs hibernate
     */
    public static void runHibernate() {
        // TODO implement hibernate
    }

    /**
     * Runs the API server
     *
     * @param host Server host
     * @param port Server port
     * @return instance of HttpServer
     */
    public static HttpServer runServer(String host, int port) {
        baseURI = String.format("http://%s:%d/api/", host, port);
        final ResourceConfig rc = new ResourceConfig().packages("cat.amd.dbapi");

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(baseURI), rc);
    }

    /**
     * Main method
     *
     * @param args Main method args
     */
    public static void main(String[] args) {
        // TODO implement main
    }
}
