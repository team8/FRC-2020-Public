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
    public Throwable ex;

    public boolean passesFilter(Set<Integer> levels, Set<String> categories) {
        if (categories.isEmpty()) {
            return levels.contains(level);
        }
        return levels.contains(level) && categories.contains(category);
    }

    public String toString() {
        return toString(1, this.timeStamp);
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
            log.append('[');
            log.append(category);
            log.append("] ");
        } else {
            log.append("[General] ");
        }

        log.append(message);

        if (ex != null) {
            StringWriter writer = new StringWriter(256);
            ex.printStackTrace(new PrintWriter(writer));
            log.append('\n');
            log.append(writer.toString().trim());
        }

        if (amount > 1) {
            log.append(" [");
            log.append(amount);
            log.append(']');
        }

        return log.toString();
    }

    public boolean equals(LogEntry o) {
        return Objects.equals(this.message, o.message) && Objects.equals(this.category, o.category) && Objects.equals(this.level, o.level) && Objects.equals(this.ex, o.ex);
    }

    public LogEntry() {
    }

    public LogEntry(long timeStamp, int level, String category, String message, Throwable ex) {
        this.timeStamp = timeStamp;
        this.level = level;
        this.category = category;
        this.message = message;
        this.ex = ex;
    }
}
