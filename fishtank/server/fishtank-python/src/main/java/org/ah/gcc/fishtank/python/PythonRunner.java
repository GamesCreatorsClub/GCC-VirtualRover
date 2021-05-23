package org.ah.gcc.fishtank.python;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class PythonRunner implements Runnable {
    private StreamGobbler errorGobbler;
    private StreamGobbler outputGobbler;
    private Process process;
    private Thread shutdownHook;

    private static class StreamGobbler implements Runnable {
        private InputStream is;
        private String type;
        private Thread thread;

        private StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void start() {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }

        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null)
                    System.out.println(type + "> " + line);
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public PythonRunner() {

    }

    public void prepareCode() throws FileNotFoundException, IOException {
        try (OutputStream out = new FileOutputStream(new File("fishtank-python"))) {
            try (InputStream in = ClassLoader.getSystemResourceAsStream("fishtank-python")) {
                byte[] buffer = new byte[10240];
                int r = in.read(buffer);
                while (r > 0) {
                    out.write(buffer, 0, r);
                    r = in.read(buffer);
                }
            }
        }
    }

    public void start() throws IOException, InterruptedException {
        Process chmodProcess = Runtime.getRuntime().exec("/bin/chmod u+x fishtank-python");
        chmodProcess.waitFor();

        ProcessBuilder processBuilder = new ProcessBuilder()
                .inheritIO()
//                .command("bash", "fishtank-python")
                .command("./fishtank-python")
                .redirectErrorStream(true);
        process = processBuilder.start();
        errorGobbler = new StreamGobbler(process.getErrorStream(), "ERR");
        outputGobbler = new StreamGobbler(process.getInputStream(), "OUT");

        outputGobbler.start();
        errorGobbler.start();

        shutdownHook = new Thread(this);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    public static void main(String[] args) throws Exception {
        PythonRunner pythonRunner = new PythonRunner();
        pythonRunner.prepareCode();
        pythonRunner.start();
        while (true) {
            Thread.sleep(1000);
        }
    }

    @Override
    public void run() {
        System.out.println("Killing process " + process);
        process.destroy();
    }
}
