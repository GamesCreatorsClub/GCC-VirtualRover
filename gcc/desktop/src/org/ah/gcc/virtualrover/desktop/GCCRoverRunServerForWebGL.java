package org.ah.gcc.virtualrover.desktop;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;

public class GCCRoverRunServerForWebGL {

    public static void main(String[] args) throws Exception {
        File path;
        if (args.length > 0) {
            path = new File(args[0]);
        } else {
            File here = new File("");
            File absoluteFile = here.getAbsoluteFile();
            String hereName = absoluteFile.getName();
            if (hereName.endsWith("-localweb")) {
                String fixedName = hereName.substring(0, hereName.length() - 9);
                path = new File(absoluteFile.getParentFile(), fixedName + "-html/war");
            } else if (hereName.endsWith("-desktop")) {
                String fixedName = hereName.substring(0, hereName.length() - 8);
                path = new File(absoluteFile.getParentFile(), fixedName + "-html/war");
            } else if (hereName.equals("desktop")) {
                String fixedName = hereName.substring(0, hereName.length() - 7);
                path = new File(absoluteFile.getParentFile(), fixedName + "html/war");
            } else {
                throw new RuntimeException("This project must be called same as main + -localweb or -desktop; got " + hereName);
            }
        }

        startServer(path, 8088);
    }

    public static void startServer(File path, int port) throws IOException {
        System.out.println("Starting server, path: " + path.getPath());
        System.out.println("  on url http://localhost:" + port);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new ServeFilesHandler(path));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class ServeFilesHandler implements HttpHandler {

        private File dir;

        public ServeFilesHandler(File dir) {
            this.dir = dir;
        }

        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            File file = new File(dir, path);

            if (file.isDirectory()) {
                File index = new File(file, "index.html");
                if (!index.exists()) {
                    StringWriter res = new StringWriter();
                    res.append("<html><body><ul>\n");
                    //noinspection ConstantConditions
                    for (String f : file.list()) {
                        res.append("<li><a href=\"" + makePath(path, f) + "\">" + makePath(path, f) + "</a></li>\n");
                    }
                    res.append("</ul></body></html>\n");
                    String response = res.toString();
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } else {
                    if (path.endsWith("/")) {
                        path = path + "index.html";
                    } else {
                        path = path + "/index.html";
                    }
                    sendFile(exchange, path);
                }
            } else {
                sendFile(exchange, path);
            }
        }

        private void sendFile(HttpExchange exchange, String path) throws IOException {
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            File file = new File(dir, path);
            boolean exists = file.exists();
            if (!exists && path.startsWith("html/")) {
                File distDir = new File(dir.getParentFile(), "build/dist");
                if (distDir.exists()) {
                    file = new File(distDir, path.substring(5));
                }
                if (!file.exists()) {
                    File gwtDir = new File(dir.getParentFile(), "build/gwt");
                    if (gwtDir.exists()) {
                        file = new File(gwtDir, "out/" + path);
                    }
                }
                if (!file.exists()) {
                    File alternative = new File(dir.getParentFile(), "war/assets/" + path.substring(5));
                    if (alternative.exists()) {
                        System.out.println("Got alternative path for " + path + " -> " + alternative.getAbsolutePath());
                        file = alternative;
                    }
                }
                exists = file.exists();
            }
            if (!exists) {
                System.out.println("Cannot find " + path + "; file=" + file.getAbsolutePath());
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
            } else {
                exchange.sendResponseHeaders(200, file.length());
                OutputStream os = exchange.getResponseBody();
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[10240];
                    int r = fis.read(buffer);
                    while (r > 0) {
                        os.write(buffer, 0, r);
                        r = fis.read(buffer);
                    }
                }
                os.close();
            }
        }

        private String makePath(String path, String file) {
            if (path.startsWith("/")) {
                return path.substring(1) + file;
            } else {
                return path + file;
            }
        }
    }
}
