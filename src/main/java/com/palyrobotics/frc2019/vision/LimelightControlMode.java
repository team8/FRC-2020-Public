package com.palyrobotics.frc2019.vision;

import java.util.HashMap;
import java.util.Map;

public class LimelightControlMode {

    public enum LedMode {
        CURRENT_PIPELINE_MODE(0),   // 0	Use the LED Mode set in the current pipeline
        FORCE_OFF(1),               // 1	Force off
        FORCE_BLINK(2),             // 2	Force blink
        FORCE_ON(3);                // 3	Force on

        private static final Map<Double, LedMode> MY_MAP = new HashMap<>();

        static {
            for (LedMode mode : values()) {
                MY_MAP.put(mode.getValue(), mode);
            }
        }

        private double value;

        LedMode(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        public static LedMode getByValue(double value) {
            return MY_MAP.get(value);
        }

        public String toString() {
            return name();
        }
    }


    public enum CamMode {
        VISION(0),
        DRIVER(1);

        private static final Map<Double, CamMode> MY_MAP = new HashMap<>();

        static {
            for (CamMode mode : values()) {
                MY_MAP.put(mode.getValue(), mode);
            }
        }

        private double value;

        CamMode(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        public static CamMode getByValue(double value) {
            return MY_MAP.get(value);
        }

        public String toString() {
            return name();
        }
    }

    public enum StreamType {
        kStandard(0),
        kPiPMain(1),
        kPiPSecondary(2);

        private static final Map<Double, StreamType> MY_MAP = new HashMap<>();

        static {
            for (StreamType streamType : values()) {
                MY_MAP.put(streamType.getValue(), streamType);
            }
        }

        private double value;

        StreamType(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        public static StreamType getByValue(double value) {
            return MY_MAP.get(value);
        }

        public String toString() {
            return name();
        }
    }

    public enum Snapshot {

        ON(1), OFF(0);

        private static final Map<Double, Snapshot> MY_MAP = new HashMap<>();

        static {
            for (Snapshot snapshot : values()) {
                MY_MAP.put(snapshot.getValue(), snapshot);
            }
        }

        private double value;

        Snapshot(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        public static Snapshot getByValue(double value) {
            return MY_MAP.get(value);
        }

        public String toString() {
            return name();
        }
    }

    public enum AdvancedTarget {

        ONE_TARGET(0), TWO_TARGETS(1), THREE_TARGETS(2);

        private static final Map<Integer, AdvancedTarget> MY_MAP = new HashMap<>();

        static {
            for (AdvancedTarget AdvancedTarget : values()) {
                MY_MAP.put(AdvancedTarget.getValue(), AdvancedTarget);
            }
        }

        private Integer value;

        AdvancedTarget(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public static AdvancedTarget getByValue(Integer value) {
            return MY_MAP.get(value);
        }

        public String toString() {
            return name();
        }
    }

    public enum AdvancedCrosshair {

        ONE(0), TWO(1);

        private static final Map<Integer, AdvancedCrosshair> MY_MAP = new HashMap<>();

        static {
            for (AdvancedCrosshair crosshair : values()) {
                MY_MAP.put(crosshair.getValue(), crosshair);
            }
        }

        private Integer value;

        AdvancedCrosshair(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public static AdvancedCrosshair getByValue(Integer value) {
            return MY_MAP.get(value);
        }

        public String toString() {
            return name();
        }
    }
}