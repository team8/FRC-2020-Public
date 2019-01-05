package com.palyrobotics.frc2018.vision;

import com.palyrobotics.frc2018.vision.util.data.SynchronizedVisionDataUnit;
import com.palyrobotics.frc2018.vision.util.data.VisionDataUnit;
import com.palyrobotics.frc2018.vision.util.synchronization.DataExistsCallback;

import java.util.concurrent.ConcurrentLinkedQueue;

public class VisionData {
	private static class DoubleExistsCallback extends DataExistsCallback<Double> {
		@Override
		public boolean exists(Double data) {
			return !(data == null || data.isNaN() || data.isInfinite());
		}
	}

	private static class ByteExistsCallback extends DataExistsCallback<byte[]> {
		@Override
		public boolean exists(byte[] data) {
			return !(data == null || data.length == 0);
		}
	}

	private static VisionDataUnit<Double> x_data = new SynchronizedVisionDataUnit<>("x_dist", Double.NaN, Double.NaN, new DoubleExistsCallback());
	private static VisionDataUnit<Double> z_data = new SynchronizedVisionDataUnit<>("z_dist", Double.NaN, Double.NaN, new DoubleExistsCallback());
	private static ConcurrentLinkedQueue<byte[]> video_queue = new ConcurrentLinkedQueue<>();

	public static VisionDataUnit<Double> getXData() {

		return x_data;
	}

	public static VisionDataUnit<Double> getZData() {

		return z_data;
	}

	public static Double getXDataValue() {

		return x_data.get();
	}

	public static Double getZDataValue() {

		return z_data.get();
	}

	public static void setXDataValue(Double x) {
		x_data.set(x);
	}

	public static void setZDataValue(Double z) {
		z_data.set(z);
	}

	public static ConcurrentLinkedQueue<byte[]> getVideoQueue() {
		return video_queue;
	}
}
