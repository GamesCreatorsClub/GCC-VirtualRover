package org.ah.gcc.virtualrover.desktop;

public class Parameters {

    private int width = 1440;
    private int height = 960;
    private boolean undecorated = true;
    private int x = 0;
    private int y = 0;
    private boolean sound = true;

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
                } else if ("--mute".equals(args[i]) || "-m".equals(args[i]) || "--no-sound".equals(args[i])) {
                    sound = false;
                    i++;
                } else if ("--sound".equals(args[i]) || "-s".equals(args[i])) {
                    sound = true;
                    i++;
                } else if ("--help".equals(args[i]) || "-h".equals(args[i]) || "-?".equals(args[i])) {
                    i++;
                    System.out.println("Possible arguments:");
                    System.out.println("");
                    System.out.println("--position or -p         position in XxY format (0x0 or 100x40). Default 0x0.");
                    System.out.println("--resollution or -r      resolution in XxY format (1280x800). Default 1440x960.");
                    System.out.println("--decorated or -d        if specified created window will have decoration. Default undecorated.");
                    System.out.println("--undecorated or -u      if specified created window will have not decoration. This is default.");
                    System.out.println("--help or -h or -?       This help.");
                    System.exit(0);
                }
            }
        }
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
}
