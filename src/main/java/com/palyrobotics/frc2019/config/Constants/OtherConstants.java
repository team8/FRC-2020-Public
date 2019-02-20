package com.palyrobotics.frc2019.config.Constants;

public class OtherConstants {
    public enum RobotName {
        VIDAR
    }

    public enum DriverName {
        BRYAN
    }

    public enum OperatorName {
        GRIFFIN
    }

    public enum FieldName {
        //we goin to cmp bois <- for sure :send-it:
        TEAM_8, TEAM_254, SVR, SVR_PRACTICE, GNR, GNR_PRACTICE, DMR, DMR_PRACTICE, CMP, CMP_PRACTICE
    }

    public static boolean operatorXBoxController = true;

    /**
     * Initialization constants
     */
    public static final RobotName kRobotName = RobotName.VIDAR;
    public static final DriverName kDriverName = DriverName.BRYAN;
    public static final OperatorName kOperatorName = OperatorName.GRIFFIN;
    public static final FieldName kFieldName = FieldName.TEAM_8;

    // Time Constants (these might exist elsewhere but whatever)
    public static final double updatesPerSecond = 50.0;
    public static final double deltaTime = 1/ updatesPerSecond;


    public static double kGroundToCarriageInches = 10.5625; // Distance from ground to the bottom of the carriage in the lowest position
    public static double kCarriageToHatchCenterInches = 6.5625; // Distance from the bottom of the carriage to the center of the hatch expel
    public static double kCarriageToCargoCenterInches = 17.1744; // Distance from the bottom of the carriage to the center of the cargo expel

    /**
     * Vision constants
     */
    // Physical constants of the limelight mount
    public static final double kLimelightElevationAngleDegrees = 30.00;
    public static final double kLimelightHeightInches = 11.00;
    // Limelight video feed dimensions
    public static final double kLimelightHeightPixels = 240;
    public static final double kLimelightWidthPixels = 320;
    // Height of the centers of vision targets
    public static final double kRocketHatchTargetHeight = 31.5 - (2.0 * Math.sin(Math.toRadians(14.5)) - 5.5 * Math.cos(Math.toRadians(14.5))) / 2.0;
    public static final double kCargoHatchTargetHeight = kRocketHatchTargetHeight;
    public static final double kLoadingHatchTargetHeight = kRocketHatchTargetHeight;
    public static final double kRocketPortTargetHeight = 39.375 - (2.0 * Math.sin(Math.toRadians(14.5)) - 5.5 * Math.cos(Math.toRadians(14.5))) / 2.0;
    // Dimensions of vision target
    public static final double kTargetHeight = 6.0;
    public static final double kTargetWidth = 14.5;
    public static final double kTargetWidthTop = 10.5;
    public static final double kTargetWidthBottom = 12.75;
    public static final double kVisionDrivePathToleranceFar = 15.0;
    public static final double kVisionDrivePathToleranceClose = 12.0;
    public static final double kVisionAlignDistanceTolerance = 1.0;
    public static final double kVisionAlignSpeedyTolerance = 1.0;

    public static final int kRequiredUltrasonicCount = 6;

}
