package loggerx;

import cn.nukkit.block.Block;
import cn.nukkit.level.Location;
import cn.nukkit.utils.TextFormat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Logger extends Thread {

    public File logFile;
    public String logPath;
    public boolean shutdown;
    public boolean isShutdown;
    public ConcurrentLinkedQueue<String> logBuffer = new ConcurrentLinkedQueue<>();
    public static Logger get;

    public Logger(String logFile) {
        get = this;
        logPath = logFile;
        initialize();
        start();
    }

    public void shutdown() {
        synchronized (this) {
            shutdown = true;
            interrupt();
            while (!isShutdown) {
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    public void print(String message) {
        logBuffer.add(message);
    }

    public void print(String message, Location pos) {
        logBuffer.add(message.replace("[x]", String.valueOf(Math.round(pos.x))).replace("[y]", String.valueOf(Math.round(pos.y))).replace("[z]", String.valueOf(Math.round(pos.z))).replace("[l]", pos.getLevel().getName()));
    }

    public void print(String message, Block b) {
        logBuffer.add(message.replace("[x]", String.valueOf(Math.round(b.x))).replace("[y]", String.valueOf(Math.round(b.y))).replace("[z]", String.valueOf(Math.round(b.z))).replace("[l]", b.getLevel().getName()));
    }

    @Override
    public void run() {
        do {
            waitForMessage();
            flushBuffer(logFile);
        } while (!shutdown);
        flushBuffer(logFile);
        synchronized (this) {
            isShutdown = true;
            notify();
        }
    }

    public void initialize() {
        logFile = new File(logPath);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {}
        }
    }

    public void waitForMessage() {
        while (logBuffer.isEmpty()) {
            try {
                synchronized (this) {
                    wait(25000);
                }
                Thread.sleep(5);
            } catch (InterruptedException ignore) {}
        }
    }

    public void flushBuffer(File logFile) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true), StandardCharsets.UTF_8), 1024);
            String fileDateFormat = new SimpleDateFormat("Y-M-d HH:mm:ss ").format(new Date());
            while (!logBuffer.isEmpty()) {
                String message = logBuffer.poll();
                if (message != null) {
                    writer.write(fileDateFormat);
                    writer.write(TextFormat.clean(message));
                    writer.write("\r\n");
                }
            }
            writer.flush();
        } catch (Exception e) {
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {}
        }
    }
}
