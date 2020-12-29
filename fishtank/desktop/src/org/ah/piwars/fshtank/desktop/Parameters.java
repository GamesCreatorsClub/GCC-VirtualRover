package org.ah.piwars.fshtank.desktop;

import org.ah.piwars.fishtank.PlatformSpecific;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Properties;

public class Parameters {

    public static final int DEFAULT_WIDTH = 1024;
    public static final int DEFAULT_HEIGHT = 768;

    public static final int DEFAULT_X = 0;
    public static final int DEFAULT_Y = 0;

    public static final boolean DEFAULT_FULLSCREEN = false;
    public static final boolean DEFAULT_UNDECORATED = false;
    public static final boolean DEFAULT_DO_DEBUG = false;
    public static final boolean DEFAULT_JOGL = false;
    public static final boolean DEFAULT_LOCAL = false;
    public static final boolean DEFAULT_TCP = false;
    public static final int DEFAULT_PORT = 7453;
    public static final PlatformSpecific.TankView DEFAULT_FISHTANK_VIEW = PlatformSpecific.TankView.FRONT;

    public static final String DEFAULT_NAME = "fishtank.properties";

    private Boolean doDebug = null;
    private Integer x = null;
    private Integer y = null;
    private Integer width = null;
    private Integer height = null;
    private Boolean undecorated = null;
    private Boolean fullScreen = null;
    private Boolean jogl = null;
    private Boolean lwjgl = null;
    private Boolean local = null;
    private Boolean tcp = null;
    private InetSocketAddress serverAddress;

    private PlatformSpecific.TankView viewTank = null;

    private String propertiesFilename = DEFAULT_NAME;

    private Properties props = new Properties();

    public Parameters() {}

    public void parseArgs(String[] args) throws IOException {
        if (args.length > 0) {
            // --resolution 800x600 --decorated --position 0x40
            int i = 0;
            while (i < args.length) {
                if ("--resolution".equals(args[i]) || "-r".equals(args[i])) {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException(args[i] + " must be followed by resolution in widthxheight format (1280x800 for instance)");
                    }
                    if (args[i + 1].indexOf('x') < 0) {
                        throw new IllegalArgumentException(args[i] + " must be followed by resolution in widthxheight format (1280x800 for instance)");
                    }
                    int xi = args[i + 1].indexOf('x');
                    width = Integer.parseInt(args[i + 1].substring(0, xi));
                    height = Integer.parseInt(args[i + 1].substring(xi + 1));
                    i = i + 2;
                } else if ("--position".equals(args[i]) || "-p".equals(args[i])) {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException(args[i] + " must be followed by position format 0x0 (100x40 for instance)");
                    }
                    if (args[i + 1].indexOf('x') < 0) {
                        throw new IllegalArgumentException(args[i] + " must be followed by position format 0x0 (100x40 for instance)");
                    }
                    int xi = args[i + 1].indexOf('x');
                    x = Integer.parseInt(args[i + 1].substring(0, xi));
                    y = Integer.parseInt(args[i + 1].substring(xi + 1));
                    i = i + 2;
                } else if ("--decorated".equals(args[i]) || "-d".equals(args[i])) {
                    undecorated = false;
                    i++;
                } else if ("--undecorated".equals(args[i]) || "-u".equals(args[i])) {
                    undecorated = true;
                    i++;
                } else if ("--fullscreen".equals(args[i]) || "--full-screen".equals(args[i]) || "-fc".equals(args[i])) {
                    fullScreen = true;
                    i++;
                } else if ("--jogl".equals(args[i])) {
                    jogl = true;
                    lwjgl = false;
                    i++;
                } else if ("--lwjgl".equals(args[i])) {
                    jogl = false;
                    lwjgl = true;
                    i++;
                } else if ("--debug".equals(args[i]) || "-d".equals(args[i])) {
                    doDebug = true;
                    i++;
                } else if ("--local".equals(args[i]) || "-lo".equals(args[i])) {
                    local = true;
                    i++;
                } else if ("--server".equals(args[i])) {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException(args[i] + " must be followed by server address. For instance TCP:127.0.0.1:7465");
                    }
                    String s = args[i + 1];
                    if (s.toUpperCase().startsWith("UDP:")) {
                        tcp = false;
                        s = s.substring(4);
                    } else if (s.toUpperCase().startsWith("TCP:")) {
                        tcp = true;
                        s = s.substring(4);
                    }
                    int j = s.indexOf(':');
                    if (j < 0) {
                        throw new IllegalArgumentException("Server address must be in <ip/dns>:<port> format but got '" + s + "'. For instance 127.0.0.1:7456.");
                    }
                    serverAddress = new InetSocketAddress(s.substring(0, j), Integer.parseInt(s.substring(j + 1)));
                    i = i + 2;
                } else if ("--tcp".equals(args[i])) {
                    tcp = true;
                } else if ("--view".equals(args[i])) {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException(args[i] + " must be followed by view name");
                    }
                    String view = args[i + 1];
                    i = i + 2;
                    if ("front".equalsIgnoreCase(view)) {
                        this.viewTank = PlatformSpecific.TankView.FRONT;
                    } else if ("left".equalsIgnoreCase(view)) {
                        this.viewTank = PlatformSpecific.TankView.LEFT;
                    } else if ("right".equalsIgnoreCase(view)) {
                        this.viewTank = PlatformSpecific.TankView.RIGHT;
                    } else {
                        throw new IllegalArgumentException("Supplied value '" + view + "' is not valid view");
                    }
                } else if ("--props".equals(args[i]) || "--properties".equals(args[i])) {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException(args[i] + " must be followed by filename");
                    }
                    propertiesFilename = args[i + 1];
                    i = i + 2;
                } else if ("--help".equals(args[i]) || "-h".equals(args[i]) || "-?".equals(args[i])) {
                    i++;
                    printHelp();
                } else {
                    printHelp();
                }
            }
        }

        load();

        if (!local && serverAddress == null) {
            throw new IllegalArgumentException("You have to select --local or set --server.");
        }
    }

    public static void printHelp() {
        System.out.println("Possible arguments:");
        System.out.println();
        System.out.println("--position or -p         position in XxY format (0x0 or 100x40). Default 0x0.");
        System.out.println("--resollution or -r      resolution in XxY format (1280x800). Default 1024x768.");
        System.out.println("--decorated or -d        if specified created window will have decoration. Default undecorated.");
        System.out.println("--undecorated or -u      if specified created window will have not decoration. This is default.");
        System.out.println("--fullscreen or -fc      full screen mode. This is not a default.");
        System.out.println("--jogl                   force running it as JOGL impl. This is not a default. (*)");
        System.out.println("--lwjgl                  force running it as LWJGL impl. This is not a default. (*)");
        System.out.println("--view                   left, front or right. Front is default");
        System.out.println("--server                 points to server machine in <ip/host>:<port> format");
        System.out.println("--tcp                    use TCP over UDP");
        System.out.println("--local or -lo           is server to be started at local machine. ");
        System.out.println("                         Mutually exclusive with --server. This is default.");
        System.out.println("--debug or -d            Switch client debugging. This is not a default.");
        System.out.println("--help or -h or -?       This help.");
        System.out.println();
        System.out.println("(*) With no --jogl or --lwjgl set it will run JOGL on arm platform (RPi) and LWJGL any other.");
        System.out.println("    Those two options are mutually exclusive.");
        System.exit(0);
    }

    public void load() throws IOException {
        File file = new File(propertiesFilename);
        if (!file.exists()) {
            file = new File(getCodePath(), propertiesFilename);
        }
        if (file.exists()) {
            System.out.println("Loading properties from " + file.getAbsolutePath());
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
            }
        } else {
            System.err.println("Could not find properties file " + file.getAbsolutePath());
        }

        doDebug = doDebug != null ? doDebug : Boolean.valueOf(props.getProperty("debug", Boolean.toString(DEFAULT_DO_DEBUG)));
        x = x != null ? x : Integer.valueOf(props.getProperty("screen.x", Integer.toString(DEFAULT_X)));
        y = y != null ? y : Integer.valueOf(props.getProperty("screen.y", Integer.toString(DEFAULT_Y)));
        width = width != null ? width : Integer.valueOf(props.getProperty("screen.width", Integer.toString(DEFAULT_WIDTH)));
        height = height != null ? height : Integer.valueOf(props.getProperty("screen.height", Integer.toString(DEFAULT_HEIGHT)));
        undecorated = undecorated != null ? undecorated : Boolean.valueOf(props.getProperty("screen.undecorated", Boolean.toString(DEFAULT_UNDECORATED)));
        fullScreen = fullScreen != null ? fullScreen : Boolean.valueOf(props.getProperty("screen.fullscreen", Boolean.toString(DEFAULT_FULLSCREEN)));

        if (jogl != null && jogl && lwjgl != null && lwjgl) {
            throw new IllegalArgumentException("You have to select --jogl or --lwjgl, but not both.");
        }

        if (jogl != null && jogl) { lwjgl = false; }
        if (lwjgl != null && lwjgl) { jogl = false; }

        if (jogl == null && lwjgl == null) {
            if (DEFAULT_JOGL) {
                jogl = true;
            } else {
                lwjgl = true;
            }
        }

        jogl = jogl != null ? jogl : Boolean.valueOf(props.getProperty("screen.jogl", Boolean.toString(DEFAULT_JOGL)));
        lwjgl = lwjgl != null ? lwjgl : Boolean.valueOf(props.getProperty("screen.lwjgl", Boolean.toString(!jogl)));

        local = local != null ? local : Boolean.valueOf(props.getProperty("server.local", Boolean.toString(DEFAULT_LOCAL)));
        tcp = tcp != null ? tcp : Boolean.valueOf(props.getProperty("server.tcp", Boolean.toString(DEFAULT_TCP)));

        viewTank = viewTank != null ? viewTank : PlatformSpecific.TankView.valueOf(props.getProperty("gfx.view", DEFAULT_FISHTANK_VIEW.toString()).toUpperCase());

        serverAddress = serverAddress != null
                ? serverAddress
                : new InetSocketAddress(
                        props.getProperty("server.host", "localhost"),
                        Integer.parseInt(props.getProperty("server.port", Integer.toString(DEFAULT_PORT))));

    }

    public Properties getProperties() {
        return props;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isUndecorated() {
        return undecorated;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public boolean isLocal() {
        return local;
    }

    public boolean doDebug() {
        return doDebug;
    }

    public InetSocketAddress getServerAddress() {
        return serverAddress;
    }

    public boolean isJOGL() {
        final String osName = System.getProperty("os.name");
        final String osArch = System.getProperty("os.arch");
        // final String osVersion = System.getProperty("os.version");
        // final String xDisplayNum = System.getenv("DISPLAY");

        final boolean isLinux = "Linux".equals(osName);
        final boolean isArm = "arm".equals(osArch);
        // final boolean noXwindows = xDisplayNum == null;

        return (jogl && !lwjgl) || (!jogl && !lwjgl && isLinux && isArm);
    }

    public boolean isLWJGL() {
        return !isJOGL();
    }

    public boolean isTCP() {
        return tcp;
    }

    public PlatformSpecific.TankView getTankView() {
        return viewTank;
    }

    private static File getCodePath() {
        String classPath = "/" + Parameters.class.getName().replace(".", "/") + ".class";
        URL resource = Parameters.class.getResource(classPath);
        System.out.println("Protocol: '" + resource.getProtocol() + "'");
        String path = resource.getFile();
        if ("jar".equals(resource.getProtocol())) {
            path = path.substring(0, path.indexOf('!'));
            if (path.startsWith("file:")) {
                path = path.substring(5);
            }
            path = path.substring(0, path.lastIndexOf('/'));
        } else {
            path = path.substring(0, path.length() - classPath.length());
        }
        if (path.endsWith("bin/main")) {
            path = path.substring(0, path.length() - 8);
        } else if (path.endsWith("target/classes")) {
            path = path.substring(0, path.length() - 14);
        } else if (path.endsWith("build/libs")) {
            path = path.substring(0, path.length() - 10);
        }
        System.out.println("Path: '" + path + "'");
        return new File(path);
    }

    public static void main(String[] args) throws Exception {
        File dir = getCodePath();
        System.out.println("Started from: " + dir.getAbsolutePath());
    }
}
