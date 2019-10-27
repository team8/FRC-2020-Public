package com.palyrobotics.frc2019.subsystems.controllers;

import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.config.constants.DrivetrainConstants;
import com.palyrobotics.frc2019.config.subsystem.DriveConfig;
import com.palyrobotics.frc2019.subsystems.Drive;
import com.palyrobotics.frc2019.subsystems.controllers.OnBoardDriveController.OnBoardControlType;
import com.palyrobotics.frc2019.subsystems.controllers.OnBoardDriveController.TrajectorySegment;
import com.palyrobotics.frc2019.util.Pose;
import com.palyrobotics.frc2019.util.SparkDriveSignal;
import com.palyrobotics.frc2019.util.config.Configs;
import com.palyrobotics.frc2019.util.trajectory.*;
import edu.wpi.first.wpilibj.Timer;

import java.util.Optional;
import java.util.Set;

/**
 * Implements an adaptive pure pursuit controller. See: https://www.ri.cmu.edu/pub_files/pub1/kelly_alonzo_1994_4/kelly_alonzo_1994_4 .pdf
 * <p>
 * Basically, we find a spot on the path we'd like to follow and calculate the wheel speeds necessary to make us land on that spot. The target spot is a
 * specified distance ahead of us, and we look further ahead the greater our tracking error.
 */
public class AdaptivePurePursuitController implements Drive.DriveController {

    private static final double kEpsilon = 1E-9;

    private double mFixedLookahead;
    private Path mPath;
    private RigidTransform2d.Delta mLastCommand;
    private Kinematics.DriveVelocity mLastDriveVelocity;
    private double mLastTime;
    private double mMaxAcceleration;
    private double mDt;
    private boolean mReversed;
    private double mPathCompletionTolerance;

    private OnBoardDriveController mOnBoardController;

    public AdaptivePurePursuitController(double fixedLookAhead, double maxAcceleration, double nominalDt, Path path, boolean reversed,
                                         double pathCompletionTolerance) {
        mFixedLookahead = fixedLookAhead;
        mMaxAcceleration = maxAcceleration;
        mPath = path;
        mDt = nominalDt;
        mLastCommand = null;
        mReversed = reversed;
        mPathCompletionTolerance = pathCompletionTolerance;
        mOnBoardController = new OnBoardDriveController(OnBoardControlType.kVelocityWithArbitraryDemand, Configs.get(DriveConfig.class).trajectoryGains);
    }

    /**
     * Calculate the next Delta and convert it to a drive signal
     *
     * @param state - the current robot state, currently unused
     * @return the left and right drive voltage outputs needed to move in the calculated Delta
     */
    @Override
    public SparkDriveSignal update(RobotState state) {

        //Get estimated robot position
        RigidTransform2d robotPose = state.getLatestFieldToVehicle().getValue();

        //Reverse the rotation if the path is reversed
        if (mReversed) {
            robotPose = new RigidTransform2d(robotPose.getTranslation(), robotPose.getRotation().rotateBy(Rotation2d.fromRadians(Math.PI)));
        }

        double distanceFromPath = mPath.update(robotPose.getTranslation());
        PathSegment.Sample lookAheadPoint = mPath.getLookaheadPoint(robotPose.getTranslation(), distanceFromPath + mFixedLookahead);
        Optional<Circle> circle = joinPath(robotPose, lookAheadPoint.translation);

        double speed = lookAheadPoint.speed;
        if (mReversed) {
            speed *= -1;
        }

        //If we are just starting the controller, use the robot's current velocity and the default dt
        double now = Timer.getFPGATimestamp();
        double dt = now - mLastTime;
        if (mLastDriveVelocity == null || mLastCommand == null) {
            mLastDriveVelocity = new Kinematics.DriveVelocity(state.drivePose.leftEncoderVelocity, state.drivePose.rightEncoderVelocity);
            mLastCommand = Kinematics.forwardKinematics(mLastDriveVelocity);
            dt = mDt;
        }

        //Ensure we don't accelerate too fast from the previous command
        double acceleration = (speed - mLastCommand.dX) / dt;
        if (acceleration < -mMaxAcceleration) {
            speed = mLastCommand.dX - mMaxAcceleration * dt;
        } else if (acceleration > mMaxAcceleration) {
            speed = mLastCommand.dX + mMaxAcceleration * dt;
        }

        //Ensure we slow down in time to stop
        //vf^2 = v^2 + 2*a*d
        //0 = v^2 + 2*a*d
        double remainingDistance = mPath.getRemainingLength();
        double maxAllowedSpeed = Math.sqrt(2 * mMaxAcceleration * remainingDistance);

        //Ensure we don't go faster than the maximum path following speed
        maxAllowedSpeed = Math.min(maxAllowedSpeed, DrivetrainConstants.kPathFollowingMaxVel);

        //Bound speed by constraints
        if (Math.abs(speed) > maxAllowedSpeed) {
            speed = maxAllowedSpeed * Math.signum(speed);
        }

        //Obtain command (linear / angular velocity)
        RigidTransform2d.Delta command;
        if (circle.isPresent()) {
            command = new RigidTransform2d.Delta(speed, 0, (circle.get().turnRight ? -1 : 1) * Math.abs(speed) / circle.get().radius);
        } else {
            command = new RigidTransform2d.Delta(speed, 0, 0);
        }

        //Convert command to set point (left / right velocity)
        Kinematics.DriveVelocity setPoint = Kinematics.inverseKinematics(command);

        //Calculate acceleration of each side based on last setPoint
        double leftAcceleration = (setPoint.left - mLastDriveVelocity.left) / dt;
        double rightAcceleration = (setPoint.right - mLastDriveVelocity.right) / dt;

//        CSVWriter.addData("lastDriveVelocityLeft", mLastDriveVelocity.left);
//        CSVWriter.addData("lastDriveVelocityRight", mLastDriveVelocity.right);

        //Pass velocity and acceleration set points into onboard controller which
        //Returns a SparkSignal with velocity setPoint and arbitrary feedforward
        TrajectorySegment leftSegment = new TrajectorySegment(setPoint.left, leftAcceleration, dt);
        TrajectorySegment rightSegment = new TrajectorySegment(setPoint.right, rightAcceleration, dt);
        try {
            mOnBoardController.updateSetPoint(leftSegment, rightSegment, this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        mLastTime = now;
        mLastCommand = command;
        mLastDriveVelocity = setPoint;

        return mOnBoardController.update(state);

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
        final Translation2d center;
        final double radius;
        final boolean turnRight;

        Circle(Translation2d center, double radius, boolean turnRight) {
            this.center = center;
            this.radius = radius;
            this.turnRight = turnRight;
        }
    }

    /**
     * Connect the current position and lookahead point with a circular arc
     *
     * @param robotPose      - the current translation and rotation of the robot
     * @param lookAheadPoint - the coordinates of the lookahead point
     * @return - A circular arc representing the path, null if the arc is degenerate
     */
    private static Optional<Circle> joinPath(RigidTransform2d robotPose, Translation2d lookAheadPoint) {
        double x1 = robotPose.getTranslation().getX();
        double y1 = robotPose.getTranslation().getY();
        double x2 = lookAheadPoint.getX();
        double y2 = lookAheadPoint.getY();

        Translation2d poseToLookahead = robotPose.getTranslation().inverse().translateBy(lookAheadPoint);
        double crossProduct = poseToLookahead.getX() * robotPose.getRotation().sin() - poseToLookahead.getY() * robotPose.getRotation().cos();
        if (Math.abs(crossProduct) < kEpsilon) {
            return Optional.empty();
        }

        double dx = x1 - x2;
        double dy = y1 - y2;
        double my = (crossProduct > 0 ? -1 : 1) * robotPose.getRotation().cos();
        double mx = (crossProduct > 0 ? 1 : -1) * robotPose.getRotation().sin();

        double crossTerm = mx * dx + my * dy;

        if (Math.abs(crossTerm) < kEpsilon) {
            //Points are collinear
            return Optional.empty();
        }

        return Optional.of(new Circle(
                new Translation2d((mx * (x1 * x1 - x2 * x2 - dy * dy) + 2 * my * x1 * dy) / (2 * crossTerm),
                        (-my * (-y1 * y1 + y2 * y2 + dx * dx) + 2 * mx * y1 * dx) / (2 * crossTerm)),
                .5 * Math.abs((dx * dx + dy * dy) / crossTerm), crossProduct > 0));
    }

    //HOPING THIS METHOD NEVER GETS CALLED
    @Override
    public Pose getSetPoint() {
        return new Pose();
    }

    // Really only used for checking point injection, ignore otherwise
    public Path getPath() {
        return mPath;
    }

    @Override
    public boolean onTarget() {
        return mPath.getRemainingLength() <= mPathCompletionTolerance;
    }

}