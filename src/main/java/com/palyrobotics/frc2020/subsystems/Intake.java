package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.robot.Commands;
import com.palyrobotics.frc2020.robot.ReadOnly;
import com.palyrobotics.frc2020.robot.RobotState;
import com.palyrobotics.frc2020.util.control.ControllerOutput;

public class Intake extends SubsystemBase {

    public enum State {
        IDLE, INTAKE, LOWER
    }

    private ControllerOutput mIntakeOutput = new ControllerOutput();
    private boolean mExtended;
    private static Intake sIntake = new Intake();

    private Intake() {}

    @Override
    public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
        switch (commands.intakeWantedState) {
            case IDLE:
                mExtended = false;
                break;
            case INTAKE:
                mExtended = false;
                mIntakeOutput.setPercentOutput(commands.intakeWantedPercentOutput);
                break;
            case LOWER:
                mExtended = true;
                break;

        }
    }

    public ControllerOutput getOutput() {
        return mIntakeOutput;
    }

    public boolean getExtendedOutput() {
        //return if extended
        return mExtended;
    }

    public static Intake getInstance() {
        return sIntake;
    }
}
