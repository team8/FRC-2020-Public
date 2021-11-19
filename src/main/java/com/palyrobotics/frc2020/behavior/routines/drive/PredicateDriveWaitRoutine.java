package com.palyrobotics.frc2020.behavior.routines.drive;

import com.palyrobotics.frc2020.behavior.routines.PredicateWaitRoutine;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import edu.wpi.first.wpilibj.geometry.Pose2d;

import java.util.function.Predicate;

public class PredicateDriveWaitRoutine extends PredicateWaitRoutine<Pose2d> {

    public PredicateDriveWaitRoutine(Predicate<Pose2d> predicate) {
        super(predicate);
    }

    @Override
    public boolean checkFinished(@ReadOnly RobotState state) {
        return super.mPredicate.test(state.drivePoseMeters);
    }
}
