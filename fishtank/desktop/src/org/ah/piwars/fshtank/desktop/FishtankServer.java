package org.ah.piwars.fshtank.desktop;

import org.ah.piwars.fishtank.server.engine.FishtankServerEngineModule;
import org.ah.themvsus.server.ServerEngineModule;
import org.ah.themvsus.server.UDPServerModule;
import org.ah.themvsus.server.WebSocketModule;
import org.ah.themvsus.server.authentication.SimpleFileRegistrationModule;
import org.ah.themvsus.server.log.LogHelper;
import org.ah.themvsus.server.mail.AbstractMailModule;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import static org.ah.piwars.fshtank.desktop.Debug.DEBUG;
import static org.ah.themvsus.server.authentication.UserManagementUtils.ensureRole;
import static org.ah.themvsus.server.authentication.UserManagementUtils.ensureUser;

import io.vertx.core.ServiceHelper;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.core.spi.VertxFactory;
import io.vertx.ext.web.Router;

public class FishtankServer {

    public static Properties config;

    public static void main(final String... args) throws Exception {
//        String configFilename = "fishtest.config";
//
//        try {
//            config = FileConfigLoader.loadConfig(configFilename);
//        } catch (FileNotFoundException e) {
//            System.err.println(e.getMessage());
//            System.exit(2);
//        } catch (IOException e) {
//            System.err.println("Failed to load config file '" + configFilename + "'.");
//            System.exit(3);
//        }

        config = new Properties();
        config.setProperty("udp.port", Integer.toString(7453));

        init(config);

        while (true) {
            Thread.sleep(100);
        }
    }

    public static void init(Properties config) throws InterruptedException {
        LogHelper.setupLogging(config);

        Logger logger = LogHelper.SERVER_LOGGER;

        boolean isTCP = false;

        int serverPort = 0;
        if (config.containsKey("udp.port")) {
            String udpPortString = config.getProperty("udp.port", "7454");
            try {
                serverPort = Integer.parseInt(udpPortString);
            } catch (NumberFormatException e) {
                logger.severe("UDP port number is not an integer; '" + udpPortString + "'");
                System.exit(4);
            }
        } else if (config.containsKey("tcp.port")) {
            isTCP = true;
            String tcpPortString = config.getProperty("tcp.port", "7454");
            try {
                serverPort = Integer.parseInt(tcpPortString);
            } catch (NumberFormatException e) {
                logger.severe("TCP port number is not an integer; '" + tcpPortString + "'");
                System.exit(4);
            }
        }
        int httpPort = 0;
        String httpPortString = config.getProperty("http.port", "8000");
        try {
            httpPort = Integer.parseInt(httpPortString);
        } catch (NumberFormatException e) {
            logger.severe("HTTP port number is not an integer; '" + httpPortString + "'");
            System.exit(5);
        }
        int httpsPort = 0;
        String httpsPortString = config.getProperty("https.port", "8001");
        try {
            httpsPort = Integer.parseInt(httpsPortString);
        } catch (NumberFormatException e) {
            logger.severe("HTTPS port number is not an integer; '" + httpPortString + "'");
            System.exit(6);
        }
        String httpContextPath = config.getProperty("http.contextPath", "/fishtank");
        String httpsContextPath = config.getProperty("https.contextPath", "/fishtank");

        logger.info("Launching server:");
        if (isTCP) {
            logger.info("    TCP server at port " + serverPort);
        } else {
            logger.info("    UDP server at port " + serverPort);
        }
        logger.info("    HTTP server at port " + httpPort);
        logger.info("    HTTP context path '" + httpContextPath + "'");
        logger.info("    HTTPS server at port " + httpsPort);
        logger.info("    HTTPS context path '" + httpsContextPath + "'");

        DEBUG.setPauseIsAllowed(true);

        VertxFactory factory = ServiceHelper.loadFactory(VertxFactory.class);
        final Vertx vertx = factory.vertx();

        final HttpServer httpServer = createHttpServer(vertx, config);
        final HttpServer httpsServer = createHttpsServer(vertx, config);

        Router httpRouter = Router.router(vertx);
        Router httpsRouter = Router.router(vertx);

        httpServer.requestHandler(httpRouter);
        httpsServer.requestHandler(httpsRouter);

        SimpleFileRegistrationModule themVsUsAuthentication = new SimpleFileRegistrationModule(
                vertx,
                new AbstractMailModule(vertx, config) { @Override public void sendEmail(String arg0, Map<String, String> arg1) { }},
                config);
        themVsUsAuthentication.init();

        createAccounts(themVsUsAuthentication);

        ServerEngineModule serverEngineModule = new FishtankServerEngineModule(vertx, themVsUsAuthentication, config);
        serverEngineModule.init();

        WebSocketModule webSocketModule = new WebSocketModule(vertx, serverEngineModule.getServerEngine(), httpServer, config);
        webSocketModule.init();

        if (isTCP) {
            throw new UnsupportedOperationException("TCP is not yet supported");
        } else {
            UDPServerModule udpServerModule = new UDPServerModule(vertx, serverEngineModule.getServerEngine(), config);
            udpServerModule.init();
        }

        httpServer.listen(httpPort);
        httpsServer.listen(httpsPort);

        logger.info("");
        logger.info("Setting up debug run...");

        logger.info("  Set server engine debug.");
        DEBUG.setServerEngine(serverEngineModule.getServerEngine());
    }

    private static void createAccounts(SimpleFileRegistrationModule authenticationModule) {
        ensureRole(authenticationModule, "GameAdmin", "CreateGame", "DeleteGame", "ListGames", "GetGameDetails");
        ensureRole(authenticationModule, "fishtank1", "ListGames", "GetGameDetails", "JoinGame");
        ensureRole(authenticationModule, "fishtank2", "ListGames", "GetGameDetails", "JoinGame");
        ensureUser(authenticationModule, "admin", "AdministratorsPassword", "SystemAdmin");
    }

    private static HttpServer createHttpsServer(Vertx vertx, Properties properties) {
        final String hostname = properties.getProperty("hostname", "localhost");
        SelfSignedCertificate certificate = SelfSignedCertificate.create(hostname);

        HttpServer httpsServer = vertx.createHttpServer(new HttpServerOptions()
                .setSsl(true)
                .setKeyCertOptions(certificate.keyCertOptions())
                .setTrustOptions(certificate.trustOptions()));
        return httpsServer;
    }

    private static HttpServer createHttpServer(final Vertx vertx, Properties properties) {
        return vertx.createHttpServer();
    }
}
