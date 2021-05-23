package org.ah.gcc.fishtank.python;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;

public class PythonRunner implements Runnable {
    private StreamGobbler errorGobbler;
    private StreamGobbler outputGobbler;
    private Process process;
    private Thread shutdownHook;
    private long processPid;
    private long myProcessId;

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

    public static synchronized long getPidOfProcess(Process p) {
        long pid = -1;

        try {
            if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
                Field f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                pid = f.getLong(p);
                f.setAccessible(false);
            }
        } catch (Exception e) {
            pid = -1;
        }
        return pid;
    }

    public static long getCurrentProcessId() throws ReflectiveOperationException, SecurityException {
        java.lang.management.RuntimeMXBean runtime = java.lang.management.ManagementFactory.getRuntimeMXBean();
        java.lang.reflect.Field jvm = runtime.getClass().getDeclaredField("jvm");
        jvm.setAccessible(true);
        sun.management.VMManagement mgmt = (sun.management.VMManagement) jvm.get(runtime);
        java.lang.reflect.Method pid_method = mgmt.getClass().getDeclaredMethod("getProcessId");
        pid_method.setAccessible(true);

        int pid = (Integer) pid_method.invoke(mgmt);
        return pid;
    }

    public void start() throws IOException, InterruptedException, SecurityException, ReflectiveOperationException {
        myProcessId = getCurrentProcessId();

        Process chmodProcess = Runtime.getRuntime().exec("/bin/chmod u+x fishtank-python");
        chmodProcess.waitFor();

        ProcessBuilder processBuilder = new ProcessBuilder()
                .inheritIO()
                .command(Arrays.asList("./fishtank-python", Long.toString(myProcessId)))
                .redirectErrorStream(true);
        process = processBuilder.start();
        errorGobbler = new StreamGobbler(process.getErrorStream(), "ERR");
        outputGobbler = new StreamGobbler(process.getInputStream(), "OUT");

        outputGobbler.start();
        errorGobbler.start();

        shutdownHook = new Thread(this);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        processPid = getPidOfProcess(process);
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
        if (processPid > 0) {
            System.out.println("Killing process with id " + processPid);
            try {
                Runtime.getRuntime().exec("/bin/kill -9 " + processPid);
            } catch (IOException e) {
                System.out.println("Failed to start kill process...");
                e.printStackTrace();
            }
        } else {
            System.out.println("Got no process id of started process");
        }
        process.destroy();
    }
}
