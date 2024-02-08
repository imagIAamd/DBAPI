package cat.amd.dbapi;

import org.glassfish.grizzly.http.server.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppMain {
    private static final Logger logger = LoggerFactory.getLogger(AppMain.class);
    private static String baseURI; // maybe can be final

    /**
     * Sets up and runs hibernate
     *
     */
    public static void runHibernate() {
        // TODO implement hibernate
    }

    /**
     * Runs the API server
     *
     * @param host
     * @param port
     * @return
     */
    public static HttpServer runServer(String host, int port) {
        // TODO implement server start up
        return null;
    }

    /**
     * Main method
     *
     * @param args
     */
    public static void main(String[] args) {
        // TODO implement main
    }
}
