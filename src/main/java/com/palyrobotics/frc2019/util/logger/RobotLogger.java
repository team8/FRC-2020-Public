package com.palyrobotics.frc2019.util.logger;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.*;
import com.esotericsoftware.minlog.Log.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

public class RobotLogger extends Logger {
    private Server server;
    private Queue<LogEntry> oldLogs = new LinkedList<>();
    private long firstLogTime = new Date().getTime();

    private final int PORT = 5802;

    public RobotLogger() {
        Log.set(1); // Controls what logs actually are shown, 1 -> everything; 7 or above, nothing

        server = new Server();
        server.getKryo().register(LogEntry.class);
        try {
            server.addListener(new Listener() {
                @Override
                public void connected(Connection connection) {
                    log(Log.LEVEL_INFO, "Logger", "Connection connected", null);
                }

                @Override
                public void disconnected(Connection connection) {
                    log(Log.LEVEL_WARN, "Logger", "Connection disconnected", null);
                }
            });
            server.bind(new InetSocketAddress(PORT), null);
            server.start();

            Log.setLogger(this);
            log(Log.LEVEL_INFO, "Logger", "Started log server", null);
        } catch (IOException | IllegalMonitorStateException exception) {
            log(Log.LEVEL_ERROR, "Logger", "Exception stack trace", exception);
        }
    }

    @Override
    public void log(int level, String category, String message, Throwable ex) {
        if (category != null && category.equals("kryo")) {
            // kyro would recursively send logs
            return;
        }
        LogEntry log = new LogEntry(new Date().getTime() - firstLogTime, level, category, message, ex);

        if (ex != null) {
            ex.printStackTrace();
        }

        oldLogs.add(log);
        if (server.getConnections().length == 0) {
            System.out.println(log.toString());
        } else {
            while (!oldLogs.isEmpty()) {
                System.out.printf("stuff was sent boi");
                server.sendToAllTCP(oldLogs.remove());
            }
        }
    }
}
