package com.palyrobotics.frc2020.behavior.routines.drive;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.palyrobotics.frc2020.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2020.config.constants.DriveConstants;
import com.palyrobotics.frc2020.config.subsystem.DriveConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;
import com.palyrobotics.frc2020.util.config.Configs;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.CentripetalAccelerationConstraint;
import edu.wpi.first.wpilibj.trajectory.constraint.TrajectoryConstraint;

@JsonSerialize (using = DrivePathRoutine.DrivePathRoutineSerializer.class)
@JsonDeserialize (using = DrivePathRoutine.DrivePathRoutineDeserializer.class)
public class DrivePathRoutine extends TimeoutRoutineBase {

	private static final DriveConfig kConfig = Configs.get(DriveConfig.class);
	private static final double kTimeoutMultiplier = 1;
	private final List<Pose2d> mPoses;
	private List<Translation2d> mWaypoints;
	private double mMaxVelocityMetersPerSecond = kConfig.pathVelocityMetersPerSecond,
			mMaxAccelerationMetersPerSecondSq = kConfig.pathAccelerationMetersPerSecondSquared;
	private double mStartingVelocityMetersPerSecond, mEndingVelocityMetersPerSecond;
	private List<TrajectoryConstraint> mConstraints = new ArrayList<>();
	private boolean mShouldReversePath, mDriveInReverse;
	private Trajectory mTrajectory;

	/**
	 * @param poses Points to move towards from current pose. No initial pose needs to be supplied.
	 */
	public DrivePathRoutine(Pose2d... poses) {
		this(Arrays.asList(poses));
	}

	/**
	 * @see #DrivePathRoutine(Pose2d...)
	 */
	public DrivePathRoutine(List<Pose2d> poses) {
		mPoses = poses;
	}

	public DrivePathRoutine startingVelocity(double velocityMetersPerSecond) {
		mStartingVelocityMetersPerSecond = velocityMetersPerSecond;
		return this;
	}

	public DrivePathRoutine addConstraint(TrajectoryConstraint constraint) {
		mConstraints.add(constraint);
		return this;
	}

	public DrivePathRoutine endingVelocity(double velocityMetersPerSecond) {
		mEndingVelocityMetersPerSecond = velocityMetersPerSecond;
		return this;
	}

	public DrivePathRoutine setMovement(double velocityMetersPerSecond, double accelerationMetersPerSecondPerSecond) {
		mMaxVelocityMetersPerSecond = velocityMetersPerSecond;
		mMaxAccelerationMetersPerSecondSq = accelerationMetersPerSecondPerSecond;
		return this;
	}

	public DrivePathRoutine limitWhen(double maxVelocityMetersPerSecond, Predicate<Pose2d> predicate) {
		mConstraints.add(new TrajectoryConstraint() {

			@Override
			public double getMaxVelocityMetersPerSecond(Pose2d poseMeters, double curvatureRadPerMeter, double velocityMetersPerSecond) {
				if (predicate.test(poseMeters)) {
					return maxVelocityMetersPerSecond;
				}
				return Double.POSITIVE_INFINITY;
			}

			@Override
			public MinMax getMinMaxAccelerationMetersPerSecondSq(Pose2d poseMeters, double curvatureRadPerMeter, double velocityMetersPerSecond) {
				return new MinMax(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			}
		});
		return this;
	}

	public DrivePathRoutine waypoints(List<Translation2d> waypoints) {
		mWaypoints = waypoints;
		return this;
	}

	/**
	 * Robot will try to drive in reverse while traversing the path. Does not reverse the path itself.
	 */
	public DrivePathRoutine driveInReverse() {
		mDriveInReverse = true;
		return this;
	}

	/**
	 * Reverse points in the path. Does not make the robot drive in reverse.
	 */
	public DrivePathRoutine reversePath() {
		mShouldReversePath = true;
		return this;
	}

	/**
	 * Reverses the path and attempts to drive it backwards. Useful for getting a robot back to its
	 * starting position after running a path.
	 */
	public DrivePathRoutine reverse() {
		driveInReverse();
		reversePath();
		return this;
	}

	public void generateTrajectory(Pose2d startingPose) {
		if (mTrajectory == null) {
			var posesWithStart = new LinkedList<>(mPoses);
			if (mShouldReversePath) {
				Collections.reverse(posesWithStart);
			}
			posesWithStart.addFirst(startingPose);
			var trajectoryConfig = DriveConstants.getTrajectoryConfig(mMaxVelocityMetersPerSecond, mMaxAccelerationMetersPerSecondSq);
			trajectoryConfig.addConstraint(new CentripetalAccelerationConstraint(1.6));
			trajectoryConfig.setReversed(mDriveInReverse);
			trajectoryConfig.setStartVelocity(mStartingVelocityMetersPerSecond);
			trajectoryConfig.setEndVelocity(mEndingVelocityMetersPerSecond);
			trajectoryConfig.addConstraints(mConstraints);
			mTrajectory = mWaypoints == null ? TrajectoryGenerator.generateTrajectory(posesWithStart, trajectoryConfig) : TrajectoryGenerator.generateTrajectory(posesWithStart.getFirst(), mWaypoints, posesWithStart.getLast(), trajectoryConfig);
			mTimeout = mTrajectory.getTotalTimeSeconds() * kTimeoutMultiplier;
		} else {
			throw new IllegalStateException("Trajectory already generated!");
		}
	}

	public Trajectory generateTrajectory() {
		var poses = new LinkedList<>(mPoses);
		if (mShouldReversePath) {
			Collections.reverse(poses);
		}
		var trajectoryConfig = DriveConstants.getTrajectoryConfig(mMaxVelocityMetersPerSecond, mMaxAccelerationMetersPerSecondSq);
		trajectoryConfig.addConstraint(new CentripetalAccelerationConstraint(1.6));
		trajectoryConfig.setReversed(mDriveInReverse);
		trajectoryConfig.setStartVelocity(mStartingVelocityMetersPerSecond);
		trajectoryConfig.setEndVelocity(mEndingVelocityMetersPerSecond);
		trajectoryConfig.addConstraints(mConstraints);

		return mWaypoints == null ? TrajectoryGenerator.generateTrajectory(poses, trajectoryConfig) : TrajectoryGenerator.generateTrajectory(poses.getFirst(), mWaypoints, poses.getLast(), trajectoryConfig);
	}

	public Trajectory getTrajectory() {
		return mTrajectory;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		// Required to start the timeout timer
		super.start(commands, state);
		generateTrajectory(state.drivePoseMeters);
	}

	@Override
	public Set<SubsystemBase> getRequiredSubsystems() {
		return Set.of(mDrive);
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		commands.setDriveFollowPath(mTrajectory);
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		// TODO: possibly implement to see if we are within a tolerance of the end early
		return false;
	}

	public List<Pose2d> getWaypoints() {
		return mPoses;
	}

	public boolean isReversed() {
		return mDriveInReverse;
	}

	static class DrivePathRoutineSerializer extends StdSerializer<DrivePathRoutine> {

		DrivePathRoutineSerializer() {
			super(DrivePathRoutine.class);
		}

		@Override
		public void serialize(DrivePathRoutine value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
			jgen.writeStartObject();
			jgen.writeObjectField("reversed", value.mDriveInReverse);
			List<Pose2d> niceUnitPoses = new ArrayList<>();
			for (Pose2d pose : value.mPoses) {
				niceUnitPoses.add(new Pose2d(pose.getTranslation().getX() * 39.37,
						pose.getTranslation().getY() * 39.37,
						new Rotation2d(pose.getRotation().getDegrees())));
			}
			jgen.writeObjectField("poseList", niceUnitPoses);
			jgen.writeEndObject();
		}
	}

	static class DrivePathRoutineDeserializer extends StdDeserializer<DrivePathRoutine> {

		DrivePathRoutineDeserializer() {
			super(DrivePathRoutine.class);
		}

		@Override
		public DrivePathRoutine deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
			JsonNode node = jp.getCodec().readTree(jp);
			Pose2d[] poseList = jp.getCodec().treeToValue(node.get("poseList"), Pose2d[].class);
			boolean isReversed = jp.getCodec().treeToValue(node.get("reversed"), boolean.class);
			List<Pose2d> meterPoses = new ArrayList<>();
			for (Pose2d pose : poseList) {
				meterPoses.add(new Pose2d(pose.getTranslation().getX() / 39.37,
						pose.getTranslation().getY() / 39.37,
						new Rotation2d(pose.getRotation().getRadians() * Math.toRadians(1))));
				System.out.println(pose.getTranslation().getX() / 39.37);
			}
			return isReversed ? new DrivePathRoutine(meterPoses).driveInReverse() :
					new DrivePathRoutine(meterPoses);
		}
	}
}
