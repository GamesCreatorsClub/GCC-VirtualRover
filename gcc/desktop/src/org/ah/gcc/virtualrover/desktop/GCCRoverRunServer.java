package org.ah.gcc.virtualrover.desktop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class GCCRoverRunServer {

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

    private static void startServer(File path, int port) throws IOException {
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

            if (!file.exists()) {
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
            } else if (file.isDirectory()) {
                File index = new File(file, "index.html");
                if (!index.exists()) {
                    StringWriter res = new StringWriter();
                    res.append("<html><body><ul>\n");
                    for (String f : file.list()) {
                        res.append("<li><a href=\"" + makePath(path, f) + "\">" + makePath(path, f) + "</a></li>\n");
                    }
                    res.append("</ul></body></html>\n");
                    String response = res.toString();
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.toString().getBytes());
                    os.close();
                } else {
                    sendFile(exchange, index);
                }
            } else {
                sendFile(exchange, file);
            }
        }

        private void sendFile(HttpExchange exchange, File file) throws IOException {
            exchange.sendResponseHeaders(200, file.length());
//            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            FileInputStream fis = new FileInputStream(file);
            try {
                byte[] buffer = new byte[10240];
                int r = fis.read(buffer);
                while (r > 0) {
                    os.write(buffer, 0, r);
                    r = fis.read(buffer);
                }
            } finally {
                fis.close();
            }
            os.close();
//            exchange.close();
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
