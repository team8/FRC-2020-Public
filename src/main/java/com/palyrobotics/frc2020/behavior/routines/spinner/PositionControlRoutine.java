package com.palyrobotics.frc2020.behavior.routines.spinner;

import com.palyrobotics.frc2020.behavior.Routine;
import com.palyrobotics.frc2020.config.subsystem.SpinnerConfig;
import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.subsystems.Spinner;
import com.palyrobotics.frc2020.subsystems.Subsystem;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

import java.util.Set;

public class PositionControlRoutine extends Routine {
    private static final SpinnerConfig mConfig = Configs.get(SpinnerConfig.class);
    private ControllerOutput mOutput = Spinner.getInstance().getOutput();
    private Spinner mSpinner = new Spinner();
    private String mCurrentColor = "", mTargetColor = "";
    private int directionToGoalColor = 0;

    protected void start() {
        mTargetColor = RobotState.getInstance().gameData;
        directionToGoalColor = mSpinner.directionToGoalColor(mCurrentColor, mTargetColor);
    }

    @Override
    public String toString() {
        return getName();
    }

    protected void update(Commands commands) {
        mCurrentColor = RobotState.getInstance().closestColorString;
        mOutput.setPercentOutput(directionToGoalColor * mConfig.positionControlOutput);
    }

    public boolean checkFinished() {
        return mTargetColor.equals(mCurrentColor);
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<Subsystem> getRequiredSubsystems() {
        return Set.of(mSpinner);
    }
}
