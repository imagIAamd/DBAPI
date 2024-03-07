package cat.amd.dbapi;

import cat.amd.dbapi.persistence.db.entities.Group;
import cat.amd.dbapi.persistence.db.entities.User;
import cat.amd.dbapi.persistence.db.managers.GroupManager;
import cat.amd.dbapi.persistence.db.managers.SessionFactoryManager;
import cat.amd.dbapi.persistence.db.managers.UserManager;
import org.apache.commons.cli.*;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static cat.amd.dbapi.Constants.*;

public class AppMain {
    private static final Logger logger = LoggerFactory.getLogger(AppMain.class);
    private static String baseURI;

    /**
     * Sets up and runs hibernate
     */
    public static void runHibernate() {
        Group administrator = new Group(ROLE_ADMINISTRATOR_NAME, "For administrators");
        Group premium = new Group(ROLE_PREMIUM_NAME, "For paid users");

        GroupManager.findGroup(administrator);
        GroupManager.findGroup(premium);

        User admin = new User("admin", "o7", "admin@admin", "56Ur,)23Avc.");
        Set<Group> groups = new HashSet<>();
        groups.add(GroupManager.findGroup(ROLE_ADMINISTRATOR_NAME));
        admin.setRoles(groups);
        UserManager.findUser(admin);
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
        Options options = new Options();

        Option hostOption = new Option("h", "host", true, "Host address. Default value: 127.0.0.1");
        hostOption.setRequired(false);
        options.addOption(hostOption);

        Option portOption = new Option("p", "port", true, "Port. Default value: 8080");
        hostOption.setRequired(false);
        options.addOption(portOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);

            String host = cmd.hasOption("host") ? cmd.getOptionValue("host") : "127.0.0.1";
            int port = cmd.hasOption("port") ? Integer.parseInt(cmd.getOptionValue("port")) : 8080;

            logger.info("Running hibernate...");
            runHibernate();

            logger.info("Running server...");
            final HttpServer server = runServer(host, port);

            String startInfo = (String.format("App started with WADL available at " +
                    "%sapplication.wadl%nPress \"Enter\" to stop it...", baseURI));
            logger.info(startInfo);

            // TODO maybe something can be done with this
            System.in.read();

            SessionFactoryManager.close();
            server.shutdownNow();

        } catch (ParseException e) {
            logger.error("Error while processing app parameters.", e);
            formatter.printHelp("utility-name", options);
            System.exit(1);

        } catch (IOException e) {
            logger.error("Error while trying to run the server.", e);
            System.exit(1);
        }

    }
}
