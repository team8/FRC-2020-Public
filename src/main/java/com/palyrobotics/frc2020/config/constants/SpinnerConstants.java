package com.palyrobotics.frc2020.config.constants;

import com.palyrobotics.frc2020.config.subsystem.SpinnerConfig;
import com.palyrobotics.frc2020.util.config.Configs;
import com.revrobotics.ColorMatch;
import edu.wpi.first.wpilibj.util.Color;

import java.util.List;

public class SpinnerConstants {
    //May need to be retuned based on lighting conditions
    private static final SpinnerConfig mSpinnerConfig = Configs.get(SpinnerConfig.class);
    public static final Color kCyanCPTarget = ColorMatch.makeColor(mSpinnerConfig.colorSensorCyanRGB.get(0), mSpinnerConfig.colorSensorCyanRGB.get(1), mSpinnerConfig.colorSensorCyanRGB.get(2));
    public static final Color kGreenCPTarget = ColorMatch.makeColor(mSpinnerConfig.colorSensorGreenRGB.get(0), mSpinnerConfig.colorSensorGreenRGB.get(1), mSpinnerConfig.colorSensorGreenRGB.get(2));
    public static final Color kRedCPTarget = ColorMatch.makeColor(mSpinnerConfig.colorSensorRedRGB.get(0), mSpinnerConfig.colorSensorRedRGB.get(1), mSpinnerConfig.colorSensorRedRGB.get(2));
    public static final Color kYellowCPTarget = ColorMatch.makeColor(mSpinnerConfig.colorSensorYellowRGB.get(0), mSpinnerConfig.colorSensorYellowRGB.get(1), mSpinnerConfig.colorSensorYellowRGB.get(2));

    public static final double idleOutput = mSpinnerConfig.idleOutput;
    public static final double rotationOutput = mSpinnerConfig.rotationOutput;
    public static final double positionOutput = mSpinnerConfig.positionOutput;
}
