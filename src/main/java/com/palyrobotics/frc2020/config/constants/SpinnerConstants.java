package com.palyrobotics.frc2020.config.constants;

import com.palyrobotics.frc2020.config.subsystem.SpinnerConfig;
import com.palyrobotics.frc2020.util.config.Configs;
import com.revrobotics.ColorMatch;
import edu.wpi.first.wpilibj.util.Color;

import java.util.List;

public class SpinnerConstants {
    //May need to be retuned based on lighting conditions
    public static final Color kCyanCPTarget = ColorMatch.makeColor(0.1, 0.4, 0.4);
    public static final Color kGreenCPTarget = ColorMatch.makeColor(0.1, 0.6, 0.25);
    public static final Color kRedCPTarget = ColorMatch.makeColor(0.5, 0.3, 0.1);
    public static final Color kYellowCPTarget = ColorMatch.makeColor(0.3, 0.5, 0.1);
}
