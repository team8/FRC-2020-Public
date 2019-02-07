package com.palyrobotics.frc2019.vision;

import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class LimelightControlMode {

    public enum LedMode {
        CURRENT_PIPELINE_MODE(0),   //0	use the LED Mode set in the current pipeline
        FORCE_OFF(1),   //1	force off
        FORCE_BLINK(2), //2	force blink
        FORCE_ON(3);    //3	force on

        private static final Map<Double, LedMode> MY_MAP = new HashMap<Double, LedMode>();

        static {
            for (LedMode mode : values()) {
                MY_MAP.put(mode.getValue(), mode);
            }
        }

        private double value;

        private LedMode(double value) {
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

        private static final Map<Double, CamMode> MY_MAP = new HashMap<Double, CamMode>();

        static {
            for (CamMode mode : values()) {
                MY_MAP.put(mode.getValue(), mode);
            }
        }

        private double value;

        private CamMode(double value) {
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

        private static final Map<Double,  StreamType> MY_MAP = new HashMap<Double,  StreamType>();

        static {
            for ( StreamType  StreamType : values()) {
                MY_MAP.put( StreamType.getValue(),  StreamType);
            }
        }

        private double value;

        private  StreamType(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        public static  StreamType getByValue(double value) {
            return MY_MAP.get(value);
        }

        public String toString() {
            return name();
        }

    }

    public enum  Snapshot {

        ON(1), OFF(0);

        private static final Map<Double,  Snapshot> MY_MAP = new HashMap<Double,  Snapshot>();

        static {
            for ( Snapshot  Snapshot : values()) {
                MY_MAP.put( Snapshot.getValue(),  Snapshot);
            }
        }

        private double value;

        private Snapshot(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        public static  Snapshot getByValue(double value) {
            return MY_MAP.get(value);
        }

        public String toString() {
            return name();
        }

    }

    public enum  Advanced_Target {

        ONE_TARGET(0), TWO_TARGETS(1), THREE_TARGETS(2);

        private static final Map<Integer,  Advanced_Target> MY_MAP = new HashMap<Integer,  Advanced_Target>();

        static {
            for ( Advanced_Target  Advanced_Target : values()) {
                MY_MAP.put( Advanced_Target.getValue(),  Advanced_Target);
            }
        }

        private Integer value;

        private Advanced_Target(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public static  Advanced_Target getByValue(Integer value) {
            return MY_MAP.get(value);
        }

        public String toString() {
            return name();
        }

    }

    public enum AdvancedCrosshair {

        ONE(0), TWO(1);

        private static final Map<Integer, AdvancedCrosshair> MY_MAP = new HashMap<Integer, AdvancedCrosshair>();

        static {
            for ( AdvancedCrosshair crosshair : values()) {
                MY_MAP.put( crosshair.getValue(),  crosshair);
            }
        }

        private Integer value;

        private AdvancedCrosshair(Integer value) {
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