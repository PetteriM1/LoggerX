package loggerx;

import cn.nukkit.Server;
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
    public String archivePath;
    public boolean archiveOldLogs;
    public boolean shutdown;
    public boolean isShutdown;
    public ConcurrentLinkedQueue<String> logBuffer = new ConcurrentLinkedQueue<>();
    public static Logger get;

    public Logger(String logPath) {
        this(logPath, null, false);
    }

    public Logger(String logPath, String archivePath, boolean archiveOldLogs) {
        get = this;
        this.logPath = logPath;
        this.archivePath = archivePath;
        this.archiveOldLogs = archiveOldLogs;
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
        String lvl = pos.level == null ? "<null>" : pos.level.getName();
        logBuffer.add(message.replace("[x]", String.valueOf(Math.round(pos.x))).replace("[y]", String.valueOf(Math.round(pos.y))).replace("[z]", String.valueOf(Math.round(pos.z))).replace("[l]", lvl));
    }

    public void print(String message, Block b) {
        String lvl = b.level == null ? "<null>" : b.level.getName();
        logBuffer.add(message.replace("[x]", String.valueOf(Math.round(b.x))).replace("[y]", String.valueOf(Math.round(b.y))).replace("[z]", String.valueOf(Math.round(b.z))).replace("[l]", lvl));
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
            } catch (IOException e) {
                Server.getInstance().getLogger().logException(e);
            }
        } else if (archiveOldLogs) {
            File oldLogs = new File(archivePath);
            if (!oldLogs.exists()) {
                oldLogs.mkdirs();
            }
            String newName = new SimpleDateFormat("y-M-d HH.mm.ss ").format(new Date(logFile.lastModified())) + logFile.getName();
            logFile.renameTo(new File(oldLogs, newName));
            logFile = new File(logPath);
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    Server.getInstance().getLogger().logException(e);
                }
            }
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
            String fileDateFormat = new SimpleDateFormat("y-M-d HH:mm:ss ").format(new Date());
            while (!logBuffer.isEmpty()) {
                String message = logBuffer.poll();
                if (message != null) {
                    writer.write(fileDateFormat);
                    writer.write(TextFormat.clean(message));
                    writer.write("\r\n");
                }
            }
            writer.flush();
        } catch (Exception ignored) {
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ignored) {}
        }
    }
}
