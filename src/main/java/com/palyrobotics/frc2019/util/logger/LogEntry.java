package com.palyrobotics.frc2019.util.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.Set;

public class LogEntry {
    private long m_TimeStamp;
    private int m_Level;
    private String m_Category;
    private String m_Message;
    private Throwable m_Cause;

    public boolean passesFilter(Set<Integer> levels, Set<String> categories) {
        if (categories.contains("all")) {
            return true;
        }
        return levels.contains(m_Level) && categories.contains(m_Category);
    }

    public String toString() {
        return toString(1, m_TimeStamp);
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
        switch (m_Level) {
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
        if (m_Category != null) {
            log.append('[').append(m_Category).append("] ");
        } else {
            log.append("[General] ");
        }
        log.append(m_Message);
        if (m_Cause != null) {
            StringWriter writer = new StringWriter(256);
            m_Cause.printStackTrace(new PrintWriter(writer));
            log.append('\n').append(writer.toString().trim());
        }

        if (amount > 1) {
            log.append(" [").append(amount).append(']');
        }

        return log.toString();
    }

    public boolean canCollapseWith(LogEntry o) {
        return Objects.equals(this.m_Message, o.m_Message) && Objects.equals(this.m_Category, o.m_Category) && Objects.equals(this.m_Level, o.m_Level) && Objects.equals(this.m_Cause, o.m_Cause);
    }

    public LogEntry() {
    }

    public LogEntry(long timeStamp, int level, String category, String message, Throwable exception) {
        this.m_TimeStamp = timeStamp;
        this.m_Level = level;
        this.m_Category = category;
        this.m_Message = message;
        this.m_Cause = exception;
    }

    public long getTimeStamp() {
        return m_TimeStamp;
    }

    public int getLevel() {
        return m_Level;
    }

    public String getCategory() {
        return m_Category;
    }

    public String getMessage() {
        return m_Message;
    }

    public Throwable getCause() {
        return m_Cause;
    }
}
