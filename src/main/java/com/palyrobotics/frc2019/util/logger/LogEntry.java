package com.palyrobotics.frc2019.util.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.Set;

public class LogEntry {
    public long timeStamp;
    public int level;
    public String category;
    public String message;
    public Throwable cause;

    public boolean passesFilter(Set<Integer> levels, Set<String> categories) {
        if (categories.contains("all")) {
            return true;
        }
        return levels.contains(level) && categories.contains(category);
    }

    public String toString() {
        return toString(1, timeStamp);
    }

    public String toString(int amount, long timeStamp) {
        StringBuilder log = new StringBuilder(256);

        long minutes = timeStamp / (1000 * 60);
        long seconds = timeStamp / (1000) % 60;
        if (minutes <= 9) log.append('0');
        log.append(minutes);
        log.append(':');
        if (seconds <= 9) log.append('0');
        log.append(seconds);
        switch (level) {
            case 5:
                log.append(" ERROR: ");
                break;
            case 4:
                log.append("  WARN: ");
                break;
            case 3:
                log.append("  INFO: ");
                break;
            case 2:
                log.append(" DEBUG: ");
                break;
            case 1:
                log.append(" TRACE: ");
                break;
            default:
                log.append(" OTHER ");
        }
        if (category != null) {
            log.append('[').append(category).append("] ");
        } else {
            log.append("[General] ");
        }
        log.append(message);
        if (cause != null) {
            StringWriter writer = new StringWriter(256);
            cause.printStackTrace(new PrintWriter(writer));
            log.append('\n').append(writer.toString().trim());
        }

        if (amount > 1) {
            log.append(" [").append(amount).append(']');
        }

        return log.toString();
    }

    public boolean canCollapseWith(LogEntry o) {
        return Objects.equals(this.message, o.message) && Objects.equals(this.category, o.category) && Objects.equals(this.level, o.level) && Objects.equals(this.cause, o.cause);
    }

    public LogEntry() {
    }

    public LogEntry(long timeStamp, int level, String category, String message, Throwable exception) {
        this.timeStamp = timeStamp;
        this.level = level;
        this.category = category;
        this.message = message;
        this.cause = exception;
    }
}
