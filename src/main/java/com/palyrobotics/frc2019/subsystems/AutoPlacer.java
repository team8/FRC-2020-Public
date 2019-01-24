package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;

/**
 * @author Jason
 */
public class AutoPlacer extends Subsystem {

    private static AutoPlacer instance = new AutoPlacer("AutoPlacer");

    public static AutoPlacer getInstance() {
        return instance;
    }

    public static void resetInstance() {
        instance = new AutoPlacer("AutoPlacer");
    }

    public boolean mOutput;

    public AutoPlacer(String name) {
        super(name);
        mOutput = false;
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        mOutput = commands.autoPlacerOutput;
    }

    public boolean getOutput() {
        return mOutput;
    }
}
