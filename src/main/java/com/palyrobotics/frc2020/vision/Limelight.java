package com.palyrobotics.frc2020.vision;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2020.config.VisionConfig;
import com.palyrobotics.frc2020.util.Util;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.vision.LimelightControlMode.*;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.XboxController;

/**
 * Wrapper around the Limelight's network tables
 */
public class Limelight {

	public static final int kOneTimesZoomPipelineId = 0, kTwoTimesZoomPipelineId = 1;
	private static String kLoggerTag = Util.classToJsonName(Limelight.class);
	private static final NetworkTableInstance sNetworkTableInstance = NetworkTableInstance.getDefault();
	private static VisionConfig kVisionConfig = Configs.get(VisionConfig.class);

	private static Limelight sInstance = new Limelight();
	private NetworkTable mTable;

	public Limelight() {
		mTable = sNetworkTableInstance.getTable("limelight");
	}

	public static Limelight getInstance() {
		return sInstance;
	}

	/**
	 * @return Whether the limelight has any valid targets (0 or 1)
	 */
	public boolean isTargetFound() {
		return mTable.getEntry("tv").getDouble(0.0) != 0.0;
	}

	public boolean isAligned() {
		return isAligned(kVisionConfig.acceptableYawError);
	}

	public boolean isAligned(double acceptableYawError) {
		return isTargetFound() && getYawToTarget() < acceptableYawError;
	}

	/**
	 * @return Horizontal Offset From Crosshair To Target (-27 degrees to 27 degrees)
	 */
	public double getYawToTarget() {
		return mTable.getEntry("tx").getDouble(0.0);
//		return Math.sin(Timer.getFPGATimestamp()) * 50;
	}

	/**
	 * @return Vertical Offset From Crosshair To Target (-20.5 degrees to 20.5 degrees)
	 */
	public double getPitchToTarget() {
		return mTable.getEntry("ty").getDouble(0.0);
	}

	/**
	 * @return Aspect ratio of width to height of the fitted bounding box
	 */
	public double getTargetAspectRatio() {
		return getTargetWidth() / getTargetLength();
	}

	/**
	 * @return Side length of shortest side of the fitted bounding box (pixels)
	 */
	public double getTargetWidth() {
		return mTable.getEntry("tshort").getDouble(0.0);
	}

	/**
	 * @return Side length of longest side of the fitted bounding box (pixels)
	 */
	public double getTargetLength() {
		return mTable.getEntry("tlong").getDouble(0.0);
	}

	/**
	 * @return Skew or rotation (-90 degrees to 0 degrees)
	 */
	public double getSkew() {
		return mTable.getEntry("ts").getDouble(0.0);
	}

	/**
	 * @return The pipeline’s latency contribution (ms) Add at least 11ms for image capture latency.
	 */
	public double getPipelineLatency() {
		return mTable.getEntry("tl").getDouble(0.0);
	}

	public LedMode getLEDMode() {
		return LedMode.getByValue(mTable.getEntry("ledMode").getDouble(0.0));
	}

	public void setLEDMode(LedMode ledMode) {
		NetworkTableEntry entry = mTable.getEntry("ledMode");
		if (Double.compare(entry.getDouble(0), ledMode.getValue()) != 0) {
			entry.setValue(ledMode.getValue());
			sNetworkTableInstance.flush();
		}
	}

	/**
	 * @return {@link CamMode} current way the camera is streaming
	 */
	public CamMode getCamMode() {
		return CamMode.getByValue(mTable.getEntry("camMode").getDouble(0.0));
	}

	/**
	 * @param camMode {@link CamMode#VISION} Run vision processing, decrease exposure, only shows
	 *                targets {@link CamMode#DRIVER} Clear video for streaming to drivers
	 */
	public void setCamMode(CamMode camMode) {
		mTable.getEntry("camMode").setValue(camMode.getValue());
	}

	/**
	 * @return Pipeline index 0-9
	 */
	public int getPipeline() {
		return (int) mTable.getEntry("pipeline").getDouble(0.0);
	}

	/**
	 * @param pipeline Pipeline index 0-9. Note that this does nothing if the limelight is set to
	 *                 override
	 */
	public void setPipeline(int pipeline) {
		if (pipeline < 0) {
			throw new IllegalArgumentException("Pipeline can not be less than zero");
		} else if (pipeline > 9) {
			throw new IllegalArgumentException("Pipeline can not be greater than nine");
		}
		mTable.getEntry("pipeline").setValue(pipeline);
	}

	public StreamType getStream() {
		return StreamType.getByValue(mTable.getEntry("stream").getDouble(0.0));
	}

	/**
	 * @param stream {@link StreamType#STANDARD} - Side-by-side streams if a web-cam is attached to
	 *               Limelight {@link StreamType#kPipMain} - The secondary camera stream is placed in
	 *               the lower-right corner of the primary camera stream
	 *               {@link StreamType#kPiPSecondary} - The primary camera stream is placed in the
	 *               lower-right corner of the secondary camera stream
	 */
	public void setStream(StreamType stream) {
		mTable.getEntry("stream").setValue(stream.getValue());
	}

	public Snapshot getSnapshot() {
		return Snapshot.getByValue(mTable.getEntry("snapshot").getDouble(0.0));
	}

	/**
	 * @param snapshot {@link Snapshot#ON} - Stop taking snapshots {@link Snapshot#OFF} - Take two
	 *                 snapshots per second
	 */
	public void setSnapshot(Snapshot snapshot) {
		mTable.getEntry("snapshot").setValue(snapshot.getValue());
	}

	/**
	 * Limelight posts three raw contours to NetworkTables that are not influenced by your grouping
	 * mode. That is, they are filtered with your pipeline parameters, but never grouped. X and Y are
	 * returned in normalized screen space (-1 to 1) rather than degrees.
	 */
	public double getYawToTargetAdvanced(AdvancedTarget raw) {
		return mTable.getEntry("tx" + raw.getValue()).getDouble(0.0);
	}

	// *************** Advanced Usage with Raw Contours *********************

	public double getPitchToTargetAdvanced(AdvancedTarget raw) {
		return mTable.getEntry("ty" + raw.getValue()).getDouble(0.0);
	}

	public double getTargetAreaAdvanced(AdvancedTarget raw) {
		return mTable.getEntry("ta" + raw.getValue()).getDouble(0.0);
	}

	public double getSkewRotationAdvanced(AdvancedTarget raw) {
		return mTable.getEntry("ts" + raw.getValue()).getDouble(0.0);
	}

	public double[] getCornerX() {
		return mTable.getEntry("tcornx").getDoubleArray(new double[0]);
	}

	public double[] getCornerY() {
		return mTable.getEntry("tcorny").getDoubleArray(new double[0]);
	}

	public double[] getRawCrosshair(AdvancedCrosshair raw) {
		var crosshairs = new double[2];
		crosshairs[0] = getRawCrosshairX(raw);
		crosshairs[1] = getRawCrosshairY(raw);
		return crosshairs;
	}

	public double getRawCrosshairX(AdvancedCrosshair raw) {
		return mTable.getEntry("cx" + raw.getValue()).getDouble(0.0);
	}

	public double getRawCrosshairY(AdvancedCrosshair raw) {
		return mTable.getEntry("cy" + raw.getValue()).getDouble(0.0);
	}

	// TODO: remove
	public double _distance;

	public XboxController _controller = new XboxController(3);

	/**
	 * Estimate z distance from camera to distance as seen at
	 * http://docs.limelightvision.io/en/latest/cs_estimating_distance.html
	 *
	 * @return Estimated distance in inches
	 */
	public double getEstimatedDistanceInches() {
//		double add = _controller.getY(GenericHID.Hand.kRight);
//		if (Math.abs(add) > 0.2) _distance += add - Math.signum(add) * 0.2;
//		return _distance;
		// Tuned 2/10/20 using lowest mounting on original design
		double oneTimesZoomAngle = 35.15378286; // pbot 36.60555401
		double twoTimesZoomAngle = 30.85378286; // pbot 31.88412543
		double a2 = getPitchToTarget();
		double h1 = 32.75;
		double h2 = 90.5;
		// Avoid divide by zero
		if (getPipeline() == kOneTimesZoomPipelineId) {
			return Math.max(0.0, ((h2 - h1) / Math.tan(Math.toRadians(oneTimesZoomAngle + a2))));
		} else if (getPipeline() == kTwoTimesZoomPipelineId) {
			return Math.max(0.0, ((h2 - h1) / Math.tan(Math.toRadians(twoTimesZoomAngle + a2))));
		} else {
			Log.warn(kLoggerTag, "Wrong pipeline used for distance estimation");
			return 0.0;
		}
	}

	/**
	 * Estimate z distance using a rational function determined with experimental data
	 */
	public double getRegressionDistanceZ() {
		// TODO: implement or remove
		return 24.6 * Math.pow(getTargetArea(), -0.64);
	}

	/**
	 * @return Target Area (0% of image to 100% of image)
	 */
	public double getTargetArea() {
		return mTable.getEntry("ta").getDouble(0.0);
	}

	// SolvePnP outputs

	public double getPnPTranslationX() {
		return mTable.getEntry("camtran").getDoubleArray(new double[] { 0, 0, 0, 0, 0, 0 })[0];
	}

	public double getPnPTranslationY() {
		return mTable.getEntry("camtran").getDoubleArray(new double[] { 0, 0, 0, 0, 0, 0 })[1];
	}

	public double getPnPPitch() {
		return mTable.getEntry("camtran").getDoubleArray(new double[] { 0, 0, 0, 0, 0, 0 })[3];
	}

	public double getPnPYaw() {
		return mTable.getEntry("camtran").getDoubleArray(new double[] { 0, 0, 0, 0, 0, 0 })[4];
	}
}
