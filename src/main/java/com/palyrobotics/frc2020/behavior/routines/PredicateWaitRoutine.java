package com.palyrobotics.frc2020.behavior.routines;

import com.palyrobotics.frc2020.behavior.RoutineBase;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Drive;
import com.palyrobotics.frc2020.subsystems.SubsystemBase;

import java.util.Set;
import java.util.function.Predicate;

/*A class whose run duration is 0 seconds; essentially runs until a certain predicate is reached. Used to break up routines*/
public abstract class PredicateWaitRoutine<T> extends RoutineBase {
    protected final Predicate<T> mPredicate;

    public PredicateWaitRoutine(Predicate<T> predicate){
        this.mPredicate = predicate;
    }

    @Override
    public abstract boolean checkFinished(@ReadOnly RobotState state);

    @Override
    public Set<SubsystemBase> getRequiredSubsystems() {
        return null;
    }
}
