package com.palyrobotics.frc2018.config;

/**
 * Contains the field distances for an alliance
 * @author Jason
 */
public class AllianceDistances {
    //Base line
    public static double kBaseLineDistanceInches = 122.0;

    //Switch
    public static double kRightSwitchX = 140.0;
    public static double kRightSwitchY = 84.5;

    public static double kLeftSwitchX = 140.25;
    public static double kLeftSwitchY = 85.75;

    public static double kPyramidFromRightY = 139.5;

    //Scales
    public static double kLeftScaleX = 298.0;
    public static double kLeftScaleY = 73.0;

    public static double kRightScaleX = 298.0;
    public static double kRightScaleY = 73.0;

    public static double kPyramidWidth = 45.0;
    public static double kPyramidLength = 40.0;

    //Offsets
    public static double kLeftCornerOffset = 29.0;
    public static double kRightCornerOffset = 29.75;

    //Length from left field wall to right edge of the exchange zone
    public static double kLeftToCenterY = 149.25;

    //Distance to the somewhat arbitrary line between the scale and the switch along which the robot drives
    public static double kScaleSwitchMidlineX = 233.0;

    public static double kLeftPlatformY = 95.25;
    public static double kRightPlatformY = 95.25;

    //Self explanatory
    public static double kFieldWidth = 324.0;
    public static double kSwitchPlateWidth = 41.75;
    public static double kSwitchPlateLength = 56.0;
    public static double kScalePlateWidth = 36.5;
    public static double kScalePlateLength = 48.0;
}
