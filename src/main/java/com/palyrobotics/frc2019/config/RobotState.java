package com.palyrobotics.frc2019.config;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.util.JoystickInput;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.XboxInput;
import com.palyrobotics.frc2019.util.trajectory.*;

import java.util.*;

/**
 * Holds all hardware input, such as sensors. <br />
 * Can be simulated
 * 
 * @author Nihar
 *
 */
public class RobotState {
	public enum GamePeriod {
		AUTO, TELEOP, DISABLED
	}

	private static RobotState instance = new RobotState();

	public double matchStartTime;

	public static RobotState getInstance() {
		return instance;
	}

	protected RobotState() {
	}

	//Updated by autoInit, teleopInit, disabledInit
	public GamePeriod gamePeriod = GamePeriod.DISABLED;
	
	//Drivetrain
	public ControlMode leftControlMode = ControlMode.Disabled;
	public ControlMode rightControlMode = ControlMode.Disabled;

	public double leftSetpoint = 0;
	public double rightSetpoint = 0;

	public double robotVelocity;
	public double robotAccel;

	//Intake
	public boolean hasCargo = false;
	public double cargoDistance = 0;
	public double intakeAngle = 0;  // Angle in degrees
	public double intakeVelocity = 0; //rpm
	public boolean hasIntakeStickyFaults = false;
	public ArrayList<Double> mLeftReadings = new ArrayList<>();
	public ArrayList<Double> mRightReadings = new ArrayList<>();

	//Pusher
	public boolean hasPusherCargo = false;
	public double cargoPusherDistance = 0;
	public ArrayList<Double> mLeftPusherReadings = new ArrayList<>();
	public ArrayList<Double> mRightPusherReadings = new ArrayList<>();

	//Hatch Intake
	public boolean hasHatch = false;
	public boolean hatchIntakeUp = true;

	//Tracks total current from kPDP
	public double shovelCurrentDraw = 0;

	//Tracks pressure in compressor
    public double compressorPressure = 0;

	//Pose stores drivetrain sensor data
	public Pose drivePose = new Pose(0, 0, 0, 0, 0, 0, 0, 0);

	//Pusher sensor data
	public double pusherCachePosition = 0;
	public double pusherPosition = 0;
	public double pusherVelocity = 0;
	public boolean hasPusherStickyFaults = false;

	//Elevator sensor data
    public double elevatorPosition = 0;
    public double elevatorVelocity = 0;
    public boolean elevatorHFX = false;

	//Robot position
	public final int kObservationBufferSize = 100;
	public final double kMaxTargetAge = 0.4;

	//FPGATimestamp -> RigidTransform2d or Rotation2d
	protected RigidTransform2d.Delta vehicle_velocity_;
	protected double differential_height_;
	protected InterpolatingTreeMap<InterpolatingDouble, RigidTransform2d> field_to_vehicle_;

	//Joystick input
	public JoystickInput leftStickInput = new JoystickInput();
	public JoystickInput rightStickInput = new JoystickInput();
	public JoystickInput climberStickInput = new JoystickInput();
	public XboxInput operatorXboxControllerInput = new XboxInput();
	public JoystickInput operatorJoystickInput = new JoystickInput();

	public synchronized void reset(double start_time, RigidTransform2d initial_field_to_vehicle) {
		field_to_vehicle_ = new InterpolatingTreeMap<>(kObservationBufferSize);
		field_to_vehicle_.put(new InterpolatingDouble(start_time), initial_field_to_vehicle);
		vehicle_velocity_ = new RigidTransform2d.Delta(0, 0, 0);
	}

	public synchronized RigidTransform2d getFieldToVehicle(double timestamp) {
		return field_to_vehicle_.getInterpolated(new InterpolatingDouble(timestamp));
	}

	public synchronized Map.Entry<InterpolatingDouble, RigidTransform2d> getLatestFieldToVehicle() {
		return field_to_vehicle_.lastEntry();
	}

	public synchronized RigidTransform2d getPredictedFieldToVehicle(double lookahead_time) {
		return getLatestFieldToVehicle().getValue().transformBy(RigidTransform2d.fromVelocity(new RigidTransform2d.Delta(vehicle_velocity_.dx * lookahead_time,
				vehicle_velocity_.dy * lookahead_time, vehicle_velocity_.dtheta * lookahead_time)));
	}

	public synchronized void addFieldToVehicleObservation(double timestamp, RigidTransform2d observation) {
		field_to_vehicle_.put(new InterpolatingDouble(timestamp), observation);
	}

	public synchronized void addObservations(double timestamp, RigidTransform2d field_to_vehicle, RigidTransform2d.Delta velocity) {
		addFieldToVehicleObservation(timestamp, field_to_vehicle);
		vehicle_velocity_ = velocity;
	}

	public RigidTransform2d generateOdometryFromSensors(double left_encoder_delta_distance, double right_encoder_delta_distance,
			Rotation2d current_gyro_angle) {
		RigidTransform2d last_measurement = getLatestFieldToVehicle().getValue();
		return Kinematics.integrateForwardKinematics(last_measurement, left_encoder_delta_distance, right_encoder_delta_distance, current_gyro_angle);
	}

	public int getNumObservations() {
		return field_to_vehicle_.size();
	}
}
