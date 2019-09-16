package com.palyrobotics.frc2019.util;

import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Quintin Dwight
 */
public class TimeDebugger {

    private static class Measurement {
        Measurement(String name, double durationSeconds) {
            this.name = name;
            this.durationSeconds = durationSeconds;
        }

        String name;
        double durationSeconds;
    }

    private String m_Name;
    private long m_ReferenceNs, m_RunningTimeNs;
    private Double m_PrintDuration;
    private ArrayList<Measurement> m_Measurements = new ArrayList<>(8);

    public TimeDebugger(String name) {
        m_Name = name;
        m_ReferenceNs = System.nanoTime();
    }

    public TimeDebugger(String name, double printDuration) {
        this(name);
        m_PrintDuration = printDuration;
    }

    public void addPoint(String name) {
        long now = System.nanoTime();
        m_RunningTimeNs += now - m_ReferenceNs;
        double deltaSeconds = (now - m_ReferenceNs) / 1e9;
        m_ReferenceNs = now;
        m_Measurements.add(new Measurement(name, deltaSeconds));
    }

    public void finish() {
        Optional.ofNullable(m_PrintDuration)
                .filter(duration -> m_RunningTimeNs > duration)
                .ifPresent(duration -> printSummary());
    }

    public void finishAndPrint() {
        printSummary();
    }

    private void printSummary() {
        System.out.printf("[Time Summary] [%s]%n", m_Name);
        for (Measurement measurement : m_Measurements) {
            System.out.printf("    <%s> %f seconds%n", measurement.name, measurement.durationSeconds);
        }
    }
}
