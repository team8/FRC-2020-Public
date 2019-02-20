package com.palyrobotics.frc2019.vision;

import com.palyrobotics.frc2019.util.trajectory.Translation2d;
import com.palyrobotics.frc2019.vision.LimelightControlMode.*;
import com.palyrobotics.frc2019.config.Constants.*;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Notifier;

/**
 *   Limelight class was started by Corey Applegate of Team 3244
 *   Granite City Gearheads. We Hope you Enjoy the Lime Light
 *   Camera.
 */
public class Limelight {

    private static Limelight instance_ = new Limelight();

    public static Limelight getInstance() {
        return instance_;
    }

    private NetworkTable m_table;
    private String m_tableName;
    private Boolean isConnected = false;
    private double _heartBeatPeriod = 0.1;

    class PeriodicRunnable implements java.lang.Runnable {
        public void run() {
            resetPilelineLatency();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            isConnected = (getPipelineLatency() == 0.0);
        }
    }

    private Notifier _heartBeat = new Notifier(new PeriodicRunnable());

    /**
     * Using the Default Lime Light NT table
     */
    public Limelight() {
        m_tableName = "limelight";
        m_table = NetworkTableInstance.getDefault().getTable(m_tableName);
        _heartBeat.startPeriodic(_heartBeatPeriod);
    }

    /**
     * If you changed the name of your Lime Light tell Me the New Name
     */
    public Limelight(String tableName) {
        m_tableName = tableName;
        m_table = NetworkTableInstance.getDefault().getTable(m_tableName);
        _heartBeat.startPeriodic(_heartBeatPeriod);
    }

    /**
     * Send an instance of the NetworkTabe
     */
    public Limelight(NetworkTable table) {
        m_table = table;
        _heartBeat.startPeriodic(_heartBeatPeriod);

    }

    //This is a test
    public boolean isConnected(){
        return isConnected;
    }

    /**
     * @return tv   Whether the limelight has any valid targets (0 or 1)
     */
    public boolean isTargetFound() {
        NetworkTableEntry tv = m_table.getEntry("tv");
        double v = tv.getDouble(0);
        if (v == 0.0f){
            return false;
        }else {
            return true;
        }
    }
    /**
     * @return tx Horizontal Offset From Crosshair To Target (-27 degrees to 27 degrees)
     */
    public double getYawToTarget() {
        NetworkTableEntry tx = m_table.getEntry("tx");
        double x = tx.getDouble(0.0);
        return x;
    }
    /**
     * @return ty Vertical Offset From Crosshair To Target (-20.5 degrees to 20.5 degrees)
     */
    public double getPitchToTarget() {
        NetworkTableEntry ty = m_table.getEntry("ty");
        double y = ty.getDouble(0.0);
        return y;
    }
    /**
     * @return tshort Sidelength of shortest side of the fitted bounding box (pixels)
     */
    public double getTargetWidth(){
        NetworkTableEntry tshort = m_table.getEntry("tshort");
        double width = tshort.getDouble(0.0);
        return width;
    }
    /**
     * @return tshort Sidelength of longest side of the fitted bounding box (pixels)
     */
    public double getTargetLength(){
        NetworkTableEntry tlong = m_table.getEntry("tlong");
        double length = tlong.getDouble(0.0);
        return length;
    }
    /**
     * @return aspect Ratio of width to height of the fitted bounding box
     */
    public double getTargetAspectRatio(){
        return getTargetWidth() / getTargetLength();
    }
    /**
     * @return ta Target Area (0% of image to 100% of image)
     */
    public double getTargetArea() {
        NetworkTableEntry ta = m_table.getEntry("ta");
        double a = ta.getDouble(0.0);
        return a;
    }
    /**
     * @return ts Skew or rotation (-90 degrees to 0 degrees)
     */
    public double getSkew() {
        NetworkTableEntry ts = m_table.getEntry("ts");
        double s = ts.getDouble(0.0);
        return s;
    }
    /**
     * @return tl The pipeline’s latency contribution (ms) Add at least 11ms for image capture latency.
     */
    public double getPipelineLatency() {
        NetworkTableEntry tl = m_table.getEntry("tl");
        double l = tl.getDouble(0.0);
        return l;
    }

    private void resetPilelineLatency(){
        m_table.getEntry("tl").setValue(0.0);
    }
    //Setters

    /**
     * LedMode  Sets limelight’s LED state
     * @param ledMode
     */
    public void setLEDMode(LedMode ledMode) {
        m_table.getEntry("ledMode").setValue(ledMode.getValue());
    }

    /**
     * @return LedMode current LED mode of the Limelight
     */
    public LedMode getLEDMode() {
        NetworkTableEntry ledMode = m_table.getEntry("ledMode");
        double led = ledMode.getDouble(0.0);
        LedMode mode = LedMode.getByValue(led);
        return mode;
    }

    /**
     * camMode  Sets Limelight’s operation mode
     *
     *  VISION
     *  DRIVER (Increases exposure, disables vision processing)
     * @param camMode
     */

    public void setCamMode(CamMode camMode) {
        m_table.getEntry("camMode").setValue(camMode.getValue());
    }

    /**
     * @return CamMode current camera mode of the Limelight
     */
    public CamMode getCamMode() {
        NetworkTableEntry camMode = m_table.getEntry("camMode");
        double cam = camMode.getDouble(0.0);
        CamMode mode = CamMode.getByValue(cam);
        return mode;
    }

    /**
     * pipeline Sets Limelight’s current pipeline
     *
     * 0 . 9	Select pipeline 0.9
     *
     * @param pipeline
     */
    public void setPipeline(Integer pipeline) {
        if(pipeline<0){
            pipeline = 0;
            throw new IllegalArgumentException("Pipeline can not be less than zero");
        }else if(pipeline>9){
            pipeline = 9;
            throw new IllegalArgumentException("Pipeline can not be greater than nine");
        }
        m_table.getEntry("pipeline").setValue(pipeline);
    }

    /**
     * Returns
     * @return pipeline current pipeline of the Lime Light
     */
    public Integer getPipelineInt(){
        NetworkTableEntry pipeline = m_table.getEntry("pipeline");
        Integer pipe = (int) pipeline.getDouble(0.0);
        return pipe;
    }

    /**
     * stream   Sets limelight’s streaming mode
     *
     * kStandard - Side-by-side streams if a webcam is attached to Limelight
     * kPiPMain - The secondary camera stream is placed in the lower-right corner of the primary camera stream
     * kPiPSecondary - The primary camera stream is placed in the lower-right corner of the secondary camera stream
     *
     * @param stream
     */
    public void setStream(StreamType stream) {
        m_table.getEntry("stream").setValue(stream.getValue());
    }

    public StreamType getStream() {
        NetworkTableEntry stream = m_table.getEntry("stream");
        double st = stream.getDouble(0.0);
        StreamType mode = StreamType.getByValue(st);
        return mode;
    }


    /**
     * snapshot Allows users to take snapshots during a match
     *
     * kon - Stop taking snapshots
     * koff - Take two snapshots per second
     * @param snapshot
     */
    public void setSnapshot(Snapshot snapshot) {
        m_table.getEntry("snapshot").setValue(snapshot.getValue());
    }

    public Snapshot getSnapshot() {
        NetworkTableEntry snapshot = m_table.getEntry("snapshot");
        double snshot = snapshot.getDouble(0.0);
        Snapshot mode = Snapshot.getByValue(snshot );
        return mode;
    }

    // *************** Advanced Usage with Raw Contours *********************

    /**
     * Limelight posts three raw contours to NetworkTables that are not influenced by your grouping mode.
     * That is, they are filtered with your pipeline parameters, but never grouped. X and Y are returned
     * in normalized screen space (-1 to 1) rather than degrees.	 *
     */

    public double getYawToTargetAdvanced(Advanced_Target raw) {
        NetworkTableEntry txRaw = m_table.getEntry("tx" + Integer.toString(raw.getValue()));
        double x = txRaw.getDouble(0.0);
        return x;
    }

    public double getPitchToTargetAdvanced(Advanced_Target raw) {
        NetworkTableEntry tyRaw = m_table.getEntry("ty" + Integer.toString(raw.getValue()));
        double y = tyRaw.getDouble(0.0);
        return y;
    }

    public double getTargetAreaAdvanced(Advanced_Target raw) {
        NetworkTableEntry taRaw = m_table.getEntry("ta" + Integer.toString(raw.getValue()));
        double a = taRaw.getDouble(0.0);
        return a;
    }

    public double getSkewRotationAdvanced(Advanced_Target raw) {
        NetworkTableEntry tsRaw = m_table.getEntry("ts" + Integer.toString(raw.getValue()));
        double s = tsRaw.getDouble(0.0);
        return s;
    }

    public double[] getCornerX(){
        NetworkTableEntry cornerXRaw = m_table.getEntry("tcornx");
        double[] cornerX = cornerXRaw.getDoubleArray(new double[0]);
        return cornerX;
    }

    public double[] getCornerY(){
        NetworkTableEntry cornerXRaw = m_table.getEntry("tcorny");
        double[] cornerY = cornerXRaw.getDoubleArray(new double[0]);
        return cornerY;
    }

    //Raw Crosshairs:
    //If you are using raw targeting data, you can still utilize your calibrated crosshairs:

    public double[] getRawCrosshair(AdvancedCrosshair raw){
        double[] crosshars = new double[2];
        crosshars[0] = getRawCrosshairX(raw);
        crosshars[1] = getRawCrosshairY(raw);
        return crosshars;
    }
    public double getRawCrosshairX(AdvancedCrosshair raw) {
        NetworkTableEntry cxRaw = m_table.getEntry("cx" + Integer.toString(raw.getValue()));
        double x = cxRaw.getDouble(0.0);
        return x;
    }

    public double getRawCrosshairY(AdvancedCrosshair raw) {
        NetworkTableEntry cyRaw = m_table.getEntry("cy" + Integer.toString(raw.getValue()));
        double y = cyRaw.getDouble(0.0);
        return y;
    }

    /**
     * Estimate z distance from camera to distance as seen at http://docs.limelightvision.io/en/latest/cs_estimating_distance.html
     * @return dist - the estimated distance
     */
    public double getEstimatedDistanceZ() {
        double a1 = OtherConstants.kLimelightElevationAngleDegrees;
        double a2 = this.getPitchToTarget();
        double h1 = OtherConstants.kLimelightHeightInches;
        double h2 = OtherConstants.kRocketHatchTargetHeight;
        double tx = this.getYawToTarget();
        //Logger.getInstance().logRobotThread(Level.INFO, "a1: " + a1 + " a2: " + a2 + " h1: " + h1 + " h2: " + h2);
        return ((h2 - h1) / Math.tan(Math.toRadians(a1 + a2))) - 10; // 10 = limelight's offset from front of robot
    }

    /**
     * Estimate z distance from camera to distance as seen at http://docs.limelightvision.io/en/latest/cs_estimating_distance.html
     * but with divide by cos(tx) to make up for difference in the distance prediction when the robot is rotated
     * (Experimental)
     * @return dist - the estimated distance
     */
    public double getCorrectedEstimatedDistanceZ() {
        double a1 = OtherConstants.kLimelightElevationAngleDegrees;
        double a2 = this.getPitchToTarget();
        double h1 = OtherConstants.kLimelightHeightInches;
        double h2 = OtherConstants.kRocketHatchTargetHeight;
        double tx = this.getYawToTarget();
        //Logger.getInstance().logRobotThread(Level.INFO, "a1: " + a1 + " a2: " + a2 + " h1: " + h1 + " h2: " + h2);
        return ((h2 - h1) / Math.tan(Math.toRadians(a1 + a2))) / Math.cos(Math.toRadians(tx)) - 10; // 10 = limelight's offset from front of robot
    }

    /**
     * Estimate z distance using a rational function determined with experimental data
     * @return
     */
    public double getRegressionDistanceZ() {
        return 24.6 * Math.pow(this.getTargetArea(), -0.64);
    }

    // SolvePnP outputs

    public double getPnPTranslationX() {
        NetworkTableEntry transform = m_table.getEntry("camtran");
        double x = transform.getDoubleArray(new double[]{0, 0, 0, 0, 0, 0})[0];
        return x;
    }

    public double getPnPTranslationY() {
        NetworkTableEntry transform = m_table.getEntry("camtran");
        double y = transform.getDoubleArray(new double[]{0, 0, 0, 0, 0, 0})[1];
        return y;
    }

    public double getPnPPitch() {
        NetworkTableEntry transform = m_table.getEntry("camtran");
        double pitch = transform.getDoubleArray(new double[]{0, 0, 0, 0, 0, 0})[3];
        return pitch;
    }

    public double getPnPYaw() {
        NetworkTableEntry transform = m_table.getEntry("camtran");
        double yaw = transform.getDoubleArray(new double[]{0, 0, 0, 0, 0, 0})[4];
        return yaw;
    }

    /**
     * Describes a vision target (position and angle)
     */
    public static class VisionTarget{
        public Translation2d position;
        public double angle;

        public VisionTarget(Translation2d position, double angle) {
            this.position = position;
            this.angle = angle;
        }
    }
}