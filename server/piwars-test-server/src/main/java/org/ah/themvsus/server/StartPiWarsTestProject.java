package org.ah.themvsus.server;

import org.ah.piwars.virtualrover.PlatformSpecific.ServerCommunicationAdapterCreatedCallback;
import org.ah.piwars.virtualrover.ServerCommunicationAdapter;
import org.ah.piwars.virtualrover.desktop.Parameters;
import org.ah.piwars.virtualrover.desktop.PiWarsRoverDesktopLauncher;
import org.ah.piwars.virtualrover.server.engine.PiWarsServerEngineModule;
import org.ah.themvsus.engine.client.CommonServerCommunicationAdapter.GameReadyCallback;
import org.ah.themvsus.engine.client.HeadlessClient;
import org.ah.themvsus.server.authentication.SimpleFileRegistrationModule;
import org.ah.themvsus.server.log.LogHelper;
import org.ah.themvsus.server.mail.MailModule;
import org.ah.themvsus.server.util.FileConfigLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ah.themvsus.engine.common.debug.Debug.DEBUG;
import static org.ah.themvsus.server.authentication.UserManagementUtils.ensureRole;
import static org.ah.themvsus.server.authentication.UserManagementUtils.ensureUser;

import static java.util.Arrays.asList;

import io.vertx.core.ServiceHelper;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.core.spi.VertxFactory;
import io.vertx.ext.web.Router;

public class StartPiWarsTestProject {

    public static Properties config;

    public static void main(final String... args) throws Exception {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        String configFilename = "themvsus.test.config";

        try {
            config = FileConfigLoader.loadConfig(configFilename);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        } catch (IOException e) {
            System.err.println("Failed to load config file '" + configFilename + "'.");
            System.exit(3);
        }

        LogHelper.setupLogging(config);

        Logger headlessLogger = Logger.getLogger("headless");
        headlessLogger.setLevel(LogHelper.levelFromString(config.getProperty("headless.level", Level.FINER.getName())));


        Logger logger = LogHelper.SERVER_LOGGER;

        int udpPort = 0;
        String udpPortString = config.getProperty("udp.port", "7454");
        try {
            udpPort = Integer.parseInt(udpPortString);
        } catch (NumberFormatException e) {
            logger.severe("UDP port number is not an integer; '" + udpPortString + "'");
            System.exit(4);
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
        String httpContextPath = config.getProperty("http.contextPath", "/themvsus");
        String httpsContextPath = config.getProperty("https.contextPath", "/themvsus");
        String filesDirString = config.getProperty("http.gameFilesDir", " ../../piwars/html/war/");

        File filesDir = new File(filesDirString);
        if (!filesDir.exists()) {
            System.err.println("Game files dir '" + filesDirString + "' does not exist.");
            System.exit(7);
        }

        logger.info("Launching server:");
        logger.info("    UDP server at port " + udpPort);
        logger.info("    HTTP server at port " + httpPort);
        logger.info("    HTTP context path '" + httpContextPath + "'");
        logger.info("    HTTPS server at port " + httpsPort);
        logger.info("    HTTPS context path '" + httpsContextPath + "'");
        logger.info("    local files in dir '" + filesDir + "'");

        DEBUG.setPauseIsAllowed(true);

        VertxFactory factory = ServiceHelper.loadFactory(VertxFactory.class);
        final Vertx vertx = factory.vertx();

        final HttpServer httpServer = createHttpServer(vertx, config);
        final HttpServer httpsServer = createHttpsServer(vertx, config);

        Router httpRouter = Router.router(vertx);
        // httpRouter.route().handler(CookieHandler.create());

        Router httpsRouter = Router.router(vertx);
        // httpsRouter.route().handler(CookieHandler.create());

        httpServer.requestHandler(httpRouter);
        httpsServer.requestHandler(httpsRouter);

        MailModule mailModule = new MailModule(vertx, config);
        mailModule.init();

        SimpleFileRegistrationModule themVsUsAuthentication = new SimpleFileRegistrationModule(vertx, mailModule, config);
        themVsUsAuthentication.init();
        themVsUsAuthentication.setupReloadSaveThread(threadFactory);

        createTestAccounts(themVsUsAuthentication);

        ServerEngineModule serverEngineModule = new PiWarsServerEngineModule(vertx, themVsUsAuthentication, config);
        serverEngineModule.init();

        RegistrationModule registrationModule = new RegistrationModule(vertx, httpsRouter, themVsUsAuthentication, config);
        registrationModule.init();

        StaticContentModule staticContentModule = new StaticContentModule(vertx, httpRouter, httpsRouter, config);
        staticContentModule.init();

        WebSocketModule webSocketModule = new WebSocketModule(vertx, serverEngineModule.getServerEngine(), httpServer, config);
        webSocketModule.init();

        UDPServerModule udpServerModule = new UDPServerModule(vertx, serverEngineModule.getServerEngine(), config);
        udpServerModule.init();

        httpServer.listen(httpPort);
        httpsServer.listen(httpsPort);

        boolean solo = false;
        boolean noui = false;
        List<String> localArgs = new ArrayList<String>(asList(args));
        if (localArgs.contains("--solo")) {
            localArgs.remove("--solo");
            solo = true;
        }
        if (localArgs.contains("--no-ui")) {
            localArgs.remove("--no-ui");
            noui = true;
        }

        startHeadlessClient("test1", "123");
        if (solo) {
            startHeadlessClient("test2", "123");
        }
        // startHeadlessClient("test3", "123");
        // startHeadlessClient("test4", "123");
        // startHeadlessClient("test5", "123");

        Parameters parameters = new Parameters();
        parameters.parseArgs(localArgs.toArray(new String[localArgs.size()]));

        TestDesktopPlatformSpecific desktopSpecific = new TestDesktopPlatformSpecific();
        desktopSpecific.setHasSound(parameters.hasSound());

        System.out.println("Setting up display as " + parameters.getWidth() + "x" + parameters.getHeight() + " @ " + parameters.getX() + ", " + parameters.getY());
        System.out.println(parameters.hasSound() ? "Set sound on" : "No sound will be loaded or played");

        desktopSpecific.setPreferredServerDetails("127.0.0.1", udpPort);


        logger.info("");
        logger.info("Setting up debug run...");

        logger.info("  Set server engine debug.");
        DEBUG.setServerEngine(serverEngineModule.getServerEngine());
        desktopSpecific.setServerCommunicationAdapterCreatedCallback(new ServerCommunicationAdapterCreatedCallback() {
            @Override public void created(ServerCommunicationAdapter serverCommunicationAdapter) {
                serverCommunicationAdapter.setGameReadyCallback(new GameReadyCallback() {
                    @Override public void gameReady() {
                        DEBUG.setClientEngine(serverCommunicationAdapter.getEngine());
                        logger.info("  Set client engine debug.");
                    }
                });
            }
        });

        if (!noui) {
            PiWarsRoverDesktopLauncher.run(parameters, desktopSpecific);
        }

        while (true) {
            Thread.sleep(100);
        }
    }

    private static void startHeadlessClient(String alias, String pass) {
        HeadlessClient headlessClient = new HeadlessClient(alias, pass, config);
        headlessClient.init();
    }

    private static void createTestAccounts(SimpleFileRegistrationModule authenticationModule) {
        ensureRole(authenticationModule, "GameAdmin", "CreateGame", "DeleteGame", "ListGames", "GetGameDetails");
        ensureRole(authenticationModule, "Player", "ListGames", "GetGameDetails", "JoinGame");
        ensureUser(authenticationModule, "admin", "AdministratorsPassword", "SystemAdmin");
        ensureUser(authenticationModule, "test1", "123", "GameAdmin", "Player");
        ensureUser(authenticationModule, "test2", "123", "Player");
//        ensureUser(authenticationModule, "test3", "123", "Player");
//        ensureUser(authenticationModule, "test4", "123", "Player");
//        ensureUser(authenticationModule, "test5", "123", "Player");
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
