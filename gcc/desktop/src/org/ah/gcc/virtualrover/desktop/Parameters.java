package org.ah.gcc.virtualrover.desktop;

import java.net.InetSocketAddress;

public class Parameters {

    private boolean doDebug = false;
    private int width = 1440;
    private int height = 960;
    private boolean undecorated = true;
    private boolean fullScreen = false;
    private int x = 0;
    private int y = 0;
    private boolean sound = true;
    private boolean jogl = false;
    private boolean lwjgl = false;
    private boolean simulation = false;
    private boolean localOnly = false;

    private InetSocketAddress serverAddress;

    public Parameters() {}

    public void parseArgs(String[] args) {
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
                } else if ("--full-screen".equals(args[i]) || "-fc".equals(args[i])) {
                    fullScreen = true;
                    i++;
                } else if ("--mute".equals(args[i]) || "-m".equals(args[i]) || "--no-sound".equals(args[i])) {
                    sound = false;
                    i++;
                } else if ("--sound".equals(args[i]) || "-s".equals(args[i])) {
                    sound = true;
                    i++;
                } else if ("--jogl".equals(args[i])) {
                    jogl = true;
                    lwjgl = false;
                    i++;
                } else if ("--lwjgl".equals(args[i])) {
                    jogl = false;
                    lwjgl = true;
                    i++;
                } else if ("--simulation".equals(args[i])) {
                    simulation = true;
                    i++;
                } else if ("--debug".equals(args[i]) || "-d".equals(args[i])) {
                    doDebug = true;
                    i++;
                } else if ("--local-only".equals(args[i]) || "-lo".equals(args[i])) {
                    localOnly = true;
                    i++;
                } else if ("--server".equals(args[i])) {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException(args[i] + " must be followed by position format 0x0 (100x40 for instance)");
                    }
                    String s = args[i + 1];
                    int j = s.indexOf(':');
                    if (j < 0) {
                        throw new IllegalArgumentException("Server address must be in <ip/dns>:<port> format but got '" + s + "'. For instance 127.0.0.1:7456.");
                    }
                    serverAddress = new InetSocketAddress(s.substring(0, j), Integer.parseInt(s.substring(j + 1)));
                    i = i + 2;
                } else if ("--help".equals(args[i]) || "-h".equals(args[i]) || "-?".equals(args[i])) {
                    i++;
                    printHelp();
                } else {
                    printHelp();
                }
            }

            if (simulation && serverAddress == null) {
                throw new IllegalArgumentException("If '--simulation' option is selected then '--server' option with address is mandatory.");
            }
        }
    }

    public static void printHelp() {
        System.out.println("Possible arguments:");
        System.out.println();
        System.out.println("--position or -p         position in XxY format (0x0 or 100x40). Default 0x0.");
        System.out.println("--resollution or -r      resolution in XxY format (1280x800). Default 1440x960.");
        System.out.println("--decorated or -d        if specified created window will have decoration. Default undecorated.");
        System.out.println("--undecorated or -u      if specified created window will have not decoration. This is default.");
        System.out.println("--full-screen or -fc     full screen mode. This is not a default.");
        System.out.println("--jogl                   force running it as JOGL impl. This is not a default. (*)");
        System.out.println("--lwjgl                  force running it as LWJGL impl. This is not a default. (*)");
        System.out.println("--simulation             Is this simulation invocation. This is not a default.");
        System.out.println("--local-only or -lo      Is this local only game. This is not a default.");
        System.out.println("--debug or -d            Switch client debugging. This is not a default.");
        System.out.println("--help or -h or -?       This help.");
        System.out.println();
        System.out.println("(*) With no --jogl or --lwjgl set it will run JOGL on arm platform (RPi) and LWJGL any other.");
        System.out.println("    Those two options are mutually exclusive.");
        System.exit(0);
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

    public boolean hasSound() {
        return sound;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public boolean isSimulation() {
        return simulation;
    }

    public boolean isLocalOnly() {
        return localOnly;
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
}
