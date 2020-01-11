package com.palyrobotics.frc2020.config.constants;

import com.palyrobotics.frc2020.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2020.util.config.Configs;

public class ClimberConstants {
    private static final ClimberConfig mClimberConfig = Configs.get(ClimberConfig.class);

    public static final float kClimberMaxHeight = mClimberConfig.kClimberMaxHeight;
    public static final float kClimberInchesPerRevolution = mClimberConfig.kClimberInchesPerRevolution;
    public static final float kClimberInchesPerMinutePerRpm = mClimberConfig.kClimberInchesPerMinutePerRpm;
}
