package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.robot.HardwareAdapter;

public class Spinner extends Subsystem {

    private SpinnerState spinnerState = SpinnerState.IDLE;
    private String currentColor;
    private String previousColor;
    private String gameColorData;
    private int colorPassed;

    public enum SpinnerState {
        TO_COLOR, SPIN, IDLE
    }

    public Spinner(String name) {
        super("spinner");
    }

    @Override
    public void start() {

    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        spinnerState = Commands.getInstance().wantedSpinnerState;
        currentColor = RobotState.getInstance().closestColorString;
        switch (spinnerState) {
            case IDLE:
                HardwareAdapter.getInstance().getSpinnerHardware().setSpinnerTalon(0);
                break;
            case SPIN:
                HardwareAdapter.getInstance().getSpinnerHardware().setSpinnerTalon(0.5);
                while (colorPassed < 30) {
                    if (!previousColor.equals(currentColor)) {
                        colorPassed++;
                    }
                }
                spinnerState = SpinnerState.IDLE;
                break;
            case TO_COLOR:
                gameColorData = RobotState.getInstance().gameData;
                HardwareAdapter.getInstance().getSpinnerHardware().setSpinnerTalon(0.25);
                while (!currentColor.equals(gameColorData)) ;
                spinnerState = SpinnerState.IDLE;
                break;
        }
        previousColor = currentColor;
    }
}
