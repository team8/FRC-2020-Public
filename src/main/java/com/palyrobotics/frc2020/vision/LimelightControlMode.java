package com.palyrobotics.frc2020.vision;

import java.util.HashMap;
import java.util.Map;

public class LimelightControlMode {

	public enum LedMode {

		CURRENT_PIPELINE_MODE(0), FORCE_OFF(1), FORCE_BLINK(2), FORCE_ON(3);

		private static final Map<Double, LedMode> kMap = new HashMap<>();

		static {
			for (LedMode mode : values()) {
				kMap.put(mode.getValue(), mode);
			}
		}

		private double value;

		LedMode(double value) {
			this.value = value;
		}

		public static LedMode getByValue(double value) {
			return kMap.get(value);
		}

		public double getValue() {
			return value;
		}

		@Override
		public String toString() {
			return name();
		}
	}

	public enum CamMode {

		VISION(0), DRIVER(1);

		private static final Map<Double, CamMode> kMap = new HashMap<>();

		static {
			for (CamMode mode : values()) {
				kMap.put(mode.getValue(), mode);
			}
		}

		private double value;

		CamMode(double value) {
			this.value = value;
		}

		public static CamMode getByValue(double value) {
			return kMap.get(value);
		}

		public double getValue() {
			return value;
		}

		@Override
		public String toString() {
			return name();
		}
	}

	public enum StreamType {

		STANDARD(0), kPipMain(1), kPiPSecondary(2);

		private static final Map<Double, StreamType> kMap = new HashMap<>();

		static {
			for (StreamType streamType : values()) {
				kMap.put(streamType.getValue(), streamType);
			}
		}

		private double value;

		StreamType(double value) {
			this.value = value;
		}

		public static StreamType getByValue(double value) {
			return kMap.get(value);
		}

		public double getValue() {
			return value;
		}

		@Override
		public String toString() {
			return name();
		}
	}

	public enum Snapshot {

		ON(1), OFF(0);

		private static final Map<Double, Snapshot> kMap = new HashMap<>();

		static {
			for (Snapshot snapshot : values()) {
				kMap.put(snapshot.getValue(), snapshot);
			}
		}

		private double value;

		Snapshot(double value) {
			this.value = value;
		}

		public static Snapshot getByValue(double value) {
			return kMap.get(value);
		}

		public double getValue() {
			return value;
		}

		@Override
		public String toString() {
			return name();
		}
	}

	public enum AdvancedTarget {

		ONE_TARGET(0), TWO_TARGETS(1), THREE_TARGETS(2);

		private static final Map<Integer, AdvancedTarget> kMap = new HashMap<>();

		static {
			for (AdvancedTarget AdvancedTarget : values()) {
				kMap.put(AdvancedTarget.getValue(), AdvancedTarget);
			}
		}

		private Integer value;

		AdvancedTarget(Integer value) {
			this.value = value;
		}

		public static AdvancedTarget getByValue(Integer value) {
			return kMap.get(value);
		}

		public Integer getValue() {
			return value;
		}

		@Override
		public String toString() {
			return name();
		}
	}

	public enum AdvancedCrosshair {

		ONE(0), TWO(1);

		private static final Map<Integer, AdvancedCrosshair> kMap = new HashMap<>();

		static {
			for (AdvancedCrosshair crosshair : values()) {
				kMap.put(crosshair.getValue(), crosshair);
			}
		}

		private Integer value;

		AdvancedCrosshair(Integer value) {
			this.value = value;
		}

		public static AdvancedCrosshair getByValue(Integer value) {
			return kMap.get(value);
		}

		public Integer getValue() {
			return value;
		}

		@Override
		public String toString() {
			return name();
		}
	}
}
