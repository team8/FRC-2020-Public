package com.palyrobotics.frc2018.subsystems.controllers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.palyrobotics.frc2018.config.Constants;
import com.palyrobotics.frc2018.config.Gains;
import com.palyrobotics.frc2018.config.RobotState;
import com.palyrobotics.frc2018.robot.Robot;
import com.palyrobotics.frc2018.subsystems.Drive;
import com.palyrobotics.frc2018.util.DriveSignal;
import com.palyrobotics.frc2018.util.Pose;
import com.palyrobotics.frc2018.util.TalonSRXOutput;
import com.palyrobotics.frc2018.util.logger.Logger;
import com.palyrobotics.frc2018.util.trajectory.*;
import com.palyrobotics.frc2018.util.trajectory.Path.Waypoint;

import edu.wpi.first.wpilibj.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

/**
 * Implements an adaptive pure pursuit controller. See: https://www.ri.cmu.edu/pub_files/pub1/kelly_alonzo_1994_4/kelly_alonzo_1994_4 .pdf
 * 
 * Improvements to the algorithm implemented from team 1712's whitepaper: https://www.chiefdelphi.com/media/papers/3488?
 * These improvements are represented by the following methods:
 * {@link #injectPoints(Path)} injectPoints}
 * {@link #smoothen(ArrayList) smoothen}
 * {@link #getCurvature(Translation2d, Translation2d, Translation2d) getCurvature}
 * {@link #setVelocities() setVelocities}
 *
 * Basically, we find a spot on the path we'd like to follow and calculate the wheel speeds necessary to make us land on that spot. The target spot is a
 * specified distance ahead of us, and we look further ahead the greater our tracking error.
 */
public class AdaptivePurePursuitController implements Drive.DriveController {

	private static final double kEpsilon = 1E-9;

	double mFixedLookahead;
	Path mPath;
	RigidTransform2d.Delta mLastCommand;
	double mLastTime;
	double mMaxAccel;
	double mDt;
	boolean mReversed;
	double mPathCompletionTolerance;

	public AdaptivePurePursuitController(double fixed_lookahead, double max_accel, double nominal_dt, Path path, boolean reversed,
			double path_completion_tolerance) {
		mFixedLookahead = fixed_lookahead;
		mMaxAccel = max_accel;
		mPath = new Path(smoothen(injectPoints(path)));
		setVelocities();
		mDt = nominal_dt;
		mLastCommand = null;
		mReversed = reversed;
		mPathCompletionTolerance = path_completion_tolerance;
	}

	/**
	 * Calculate the robot's required Delta (movement along an arc) based on the current robot pose and lookahead point.
	 * 
	 * @param robot_pose
	 *            - the current position of the robot denoted by its translation and rotation from the original position
	 * @param now
	 *            - the current timestamp
	 * @return the target x translation dx, y translation dy, and rotation dtheta
	 */
	public RigidTransform2d.Delta update(RigidTransform2d robot_pose, double now) {
		RigidTransform2d pose = robot_pose;
		if(mReversed) {
			pose = new RigidTransform2d(robot_pose.getTranslation(), robot_pose.getRotation().rotateBy(Rotation2d.fromRadians(Math.PI)));
		}
		
		double distance_from_path = mPath.update(robot_pose.getTranslation());

		PathSegment.Sample lookahead_point = mPath.getLookaheadPoint(robot_pose.getTranslation(), distance_from_path + mFixedLookahead);
//		System.out.println("Current point = " + robot_pose.getTranslation() + " " + "Lookahead point = " + lookahead_point.translation);
		//if (!mPath.getWaypoints().isEmpty()) System.out.println("First point = " + mPath.getWaypoints().get(0).position.toString());
		
		Optional<Circle> circle = joinPath(pose, lookahead_point.translation);

		double speed = lookahead_point.speed;
		//Logger.getInstance().logSubsystemThread(Level.INFO, "APP", "speed before scaling = " + speed);
		if(mReversed) {
			speed *= -1;
		}
		//Ensure we don't accelerate too fast from the previous command
		double dt = now - mLastTime;
		//Logger.getInstance().logSubsystemThread(Level.INFO, "APP", "dt = " + dt);
		if(mLastCommand == null) {
			mLastCommand = new RigidTransform2d.Delta(0, 0, 0);
			dt = mDt;
		}
		//System.out.println("Last command = " + mLastCommand.dx);
		double accel = (speed - mLastCommand.dx) / dt;
		//System.out.println("Spood = " + speed);
		//System.out.println("Accel = " + accel);
		if(accel < -mMaxAccel) {
			speed = mLastCommand.dx - mMaxAccel * dt;
		} else if(accel > mMaxAccel) {
			speed = mLastCommand.dx + mMaxAccel * dt;
		}
		//System.out.println("Scaled spood = " + speed);

		//Ensure we slow down in time to stop
		//vf^2 = v^2 + 2*a*d
		//0 = v^2 + 2*a*d
		double remaining_distance = mPath.getRemainingLength();
		//Logger.getInstance().logSubsystemThread(Level.INFO, "APP", "remaining distance = " + remaining_distance);
		double max_allowed_speed = Math.sqrt(2 * mMaxAccel * remaining_distance);
		//Logger.getInstance().logSubsystemThread(Level.INFO, "APP", "max allowed speed = " + max_allowed_speed);
		if(Math.abs(speed) > max_allowed_speed) {
			speed = max_allowed_speed * Math.signum(speed);
		}
		
		/*final double kMinSpeed = 4.0;
		if(Math.abs(speed) < kMinSpeed) {
			//Hack for dealing with problems tracking very low speeds with
			//Talons
			speed = kMinSpeed * Math.signum(speed);
		}*/

		RigidTransform2d.Delta rv;
		if(circle.isPresent()) {
			rv = new RigidTransform2d.Delta(speed, 0, (circle.get().turn_right ? -1 : 1) * Math.abs(speed) / circle.get().radius);
		} else {
			rv = new RigidTransform2d.Delta(speed, 0, 0);
		}
		mLastTime = now;
		mLastCommand = rv;
		//Logger.getInstance().logSubsystemThread(Level.INFO, "AdaptivePurePursuit", "Speed output: " + speed);
		return rv;
	}

	/**
	 * Returns the list of labeled waypoints (markers) that the robot has passed
	 * 
	 * @return a set of Strings representing each of the crossed markers
	 */
	public Set<String> getMarkersCrossed() {
		return mPath.getMarkersCrossed();
	}

	//An abstraction of a circular arc used for turning motion
	public static class Circle {
		public final Translation2d center;
		public final double radius;
		public final boolean turn_right;

		public Circle(Translation2d center, double radius, boolean turn_right) {
			this.center = center;
			this.radius = radius;
			this.turn_right = turn_right;
		}
	}

	/**
	 * Connect the current position and lookahead point with a circular arc
	 * 
	 * @param robot_pose
	 *            - the current translation and rotation of the robot
	 * @param lookahead_point
	 *            - the coordinates of the lookahead point
	 * @return - A circular arc representing the path, null if the arc is degenerate
	 */
	public static Optional<Circle> joinPath(RigidTransform2d robot_pose, Translation2d lookahead_point) {
		double x1 = robot_pose.getTranslation().getX();
		double y1 = robot_pose.getTranslation().getY();
		double x2 = lookahead_point.getX();
		double y2 = lookahead_point.getY();

		Translation2d pose_to_lookahead = robot_pose.getTranslation().inverse().translateBy(lookahead_point);
		double cross_product = pose_to_lookahead.getX() * robot_pose.getRotation().sin() - pose_to_lookahead.getY() * robot_pose.getRotation().cos();
		if(Math.abs(cross_product) < kEpsilon) {
			return Optional.empty();
		}

		double dx = x1 - x2;
		double dy = y1 - y2;
		double my = (cross_product > 0 ? -1 : 1) * robot_pose.getRotation().cos();
		double mx = (cross_product > 0 ? 1 : -1) * robot_pose.getRotation().sin();

		double cross_term = mx * dx + my * dy;

		if(Math.abs(cross_term) < kEpsilon) {
			//Points are collinear
			return Optional.empty();
		}

		return Optional.of(new Circle(
				new Translation2d((mx * (x1 * x1 - x2 * x2 - dy * dy) + 2 * my * x1 * dy) / (2 * cross_term),
						(-my * (-y1 * y1 + y2 * y2 + dx * dx) + 2 * mx * y1 * dx) / (2 * cross_term)),
				.5 * Math.abs((dx * dx + dy * dy) / cross_term), cross_product > 0));
	}

	/**
	 * Determine curvature of the circular arc connecting the current waypoint with those directly before and after it
	 * Used for curvature calculation and adjustments to velocity around turns
	 *
	 * @param previous
	 *            - the first waypoint in the path to be connected
	 * @param current
	 *            - the second waypoint to be connected. The curvature calculation applies to this point
	 * @param next
	 * 			  - the third waypoint in the path to be connected
	 * @return - A circular arc representing the path, null if the arc is degenerate
	 */
	public static double getCurvature(Translation2d previous, Translation2d current, Translation2d next) {
		double x1 = previous.getX();
		double y1 = previous.getY();
		double x2 = current.getX();
		double y2 = current.getY();
		double x3 = next.getX();
		double y3 = next.getY();

		// Avoid a divide by zero error
		if (x1 == x2) x1 += .001;
		double k1 = 0.5 * (x1 * x1 + y1 * y1 - x2 * x2 - y2 * y2)/(x1 - x2);
		double k2 = (y1 - y2)/(x1 - x2);
		double b = 0.5 * (x2 * x2 - 2 * x2 * k1 + y2 * y2 - x3 * x3 + 2 * x3 * k1 - y3 * y3)/(x3 * k2 - y3 + y2 - x2 * k2);
		double a = k1 - k2 * b;

		double radius = Math.sqrt(Math.pow(x1 - a, 2) + Math.pow(y1 - b, 2));
		if (Double.isNaN(radius)) return 0;
		return 1 / radius;

	}

	/**
	 * Calculate the next Delta and convert it to a drive signal
	 * 
	 * @param state
	 *            - the current robot state, currently unused
	 * @return the left and right drive voltage outputs needed to move in the calculated Delta
	 */
	@Override
	public DriveSignal update(RobotState state) {
		RigidTransform2d robot_pose = Robot.getRobotState().getLatestFieldToVehicle().getValue();
		//Logger.getInstance().logSubsystemThread(Level.FINEST, robot_pose);
		RigidTransform2d.Delta command = this.update(robot_pose, Timer.getFPGATimestamp());
		Kinematics.DriveVelocity setpoint = Kinematics.inverseKinematics(command);
		setpoint = new Kinematics.DriveVelocity(setpoint.left, setpoint.right);
		//System.out.println("Left setpoint = " + setpoint.left);
		//System.out.println("Right setpoint = " + setpoint.right);
		//Scale the command to respect the max velocity limits
		double max_vel = 0.0;
		max_vel = Math.max(max_vel, Math.abs(setpoint.left));
		max_vel = Math.max(max_vel, Math.abs(setpoint.right));
		//Logger.getInstance().logSubsystemThread(Level.INFO, "APP", "max_vel = " + max_vel);
		if(max_vel > Constants.kPathFollowingMaxVel) {
			//System.out.println("This thing is too damn fast");
			double scaling = Constants.kPathFollowingMaxVel / max_vel;
			setpoint = new Kinematics.DriveVelocity(setpoint.left * scaling, setpoint.right * scaling);
		}

		final TalonSRXOutput left = new TalonSRXOutput(ControlMode.Velocity, Gains.forsetiVelocity, setpoint.left * Constants.kDriveSpeedUnitConversion),
				right = new TalonSRXOutput(ControlMode.Velocity, Gains.forsetiVelocity, setpoint.right * Constants.kDriveSpeedUnitConversion);
//		System.out.println("Left output = " + left + " " + "Right output = " + right);
		return new DriveSignal(left, right);
	}

	// Add intermediate, closely-spaced points between waypoints to improve following
	private ArrayList<Waypoint> injectPoints(Path path) {
		// Path to insert points into
		ArrayList<Waypoint> newPoints = new ArrayList<>(path.getWaypoints());
		ArrayList<PathSegment> segments = new ArrayList<>(path.getSegments());

		int index = 0; // Keep track of the index in newPoints to insert points into

		for (PathSegment segment : segments) {
			Translation2d start = segment.getStart();
			Translation2d end = segment.getEnd();
			// Portion of the segment's length occupied by one interval; needed for the interpolate() function
			double interpolateX = Constants.kInsertionSpacingInches / segment.getLength();
			// Insert points on the segment using interpolation
			for (double i = interpolateX; i < 1; i += interpolateX) {
				newPoints.add(index++, new Waypoint(start.interpolate(end, i),segment.getSpeed()));
			}
			// Increment pointer so it inserts between the next pair of waypoints
			index++;
		}
		return newPoints;
	}

	// Smooth the path using gradient descent; inspired by https://github.com/KHEngineering/SmoothPathPlanner/blob/11059aa2ec314ba20b364aeea3c968aca2672b49/src/usfirst/frc/team2168/robot/FalconPathPlanner.java#L214
	private ArrayList<Waypoint> smoothen(ArrayList<Waypoint> pts) {
		ArrayList<Waypoint> oldPts = new ArrayList<>(pts);
		ArrayList<Waypoint> newPts = new ArrayList<>(pts);
		// Sum of the horizontal and vertical shifts of each point in one iteration of smoothing
		double change = Constants.kSmoothingTolerance;
		int numIters = 0;
		// Perform smoothing passes until convergence
		while (change >= Constants.kSmoothingTolerance && numIters <= Constants.kSmoothingMaxIters) {
			change = 0.0;
			// Smooth all points except for the final one
			// We deleted the first waypoint at (0, 0) but for smoothing code to work we have to pretend it's still there
			for (int i = 0; i < oldPts.size() - 1; i++) {
				double x1 = (i > 0) ? newPts.get(i-1).position.getX() : 0;
				double y1 = (i > 0) ? newPts.get(i-1).position.getY() : 0;
				double x2 = newPts.get(i).position.getX();
				double y2 = newPts.get(i).position.getY();
				double x3 = newPts.get(i+1).position.getX();
				double y3 = newPts.get(i+1).position.getY();
				double x4 = oldPts.get(i).position.getX();
				double y4 = oldPts.get(i).position.getY();

				double changeX = Constants.kSmoothingWeightData * (x4 - x2) + Constants.kSmoothingWeight * (x1 + x3 - (2.0 * x2));
				double changeY = Constants.kSmoothingWeightData * (y4 - y2) + Constants.kSmoothingWeight * (y1 + y3 - (2.0 * y2));
				x2 += changeX;
				y2 += changeY;
				newPts.set(i, new Waypoint(new Translation2d(x2, y2), oldPts.get(i).speed));
				change += Math.abs(changeX + changeY);	
			}
			numIters++;
		}

		return newPts;
	}

	/*
	 * Automatically calculate target velocities throughout the path. For each waypoint before the last one,
	 * the velocity is the smaller value between Constants.kPathFollowingMaxVel and k/curvature, where k is an arbitrary constant.
	 */

	public void setVelocities() {
		List<Waypoint> points = mPath.getWaypoints();
		for (int i = 0; i < points.size() - 1; i++) {
			Translation2d previous = (i == 0) ? new Translation2d(0, 0) : points.get(i - 1).position;
			Translation2d current = points.get(i).position;
			Translation2d next = points.get(i+1).position;
			double maxVel = Math.min(Constants.kPathFollowingMaxVel, Constants.kTurnVelocityReduction/getCurvature(previous, current, next));
			points.set(i, new Waypoint(current, maxVel));
		}
	}


	//HOPING THIS METHOD NEVER GETS CALLED
	@Override
	public Pose getSetpoint() {
		Pose setpoint = new Pose(0, 0, 0, 0, 0, 0, 0, 0);
		return setpoint;
	}

	// Really only used for checking point injection, ignore otherwise
	public Path getPath() {
		return mPath;
	}

	@Override
	public boolean onTarget() {
		/*
		 * RigidTransform2d pose = RobotPosition.getInstance().getLatestFieldToVehicle().getValue(); if (pose.getTranslation().getX() > 40) return true;
		 */
		double remainingLength = mPath.getRemainingLength();
		//System.out.println("remaining length = " + remainingLength);
		return remainingLength <= mPathCompletionTolerance;
	}

}