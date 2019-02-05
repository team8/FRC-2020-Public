package com.palyrobotics.frc2019.config.Constants;

public class PortConstants {
    //DRIVETRAIN
    //CAN BUS slots for drivetrain
    public static final int kVidarLeftDriveMasterDeviceID = 0;
    public static final int kVidarLeftDriveSlave1DeviceID = 1;
    public static final int kVidarLeftDriveSlave2DeviceID = 2;

    public static final int kVidarRightDriveMasterDeviceID = 15;
    public static final int kVidarRightDriveSlave1DeviceID = 14;
    public static final int kVidarRightDriveSlave2DeviceID = 13;

    //ELEVATOR
    public static final int kVidarElevatorMasterSparkID = 0;
    public static final int kVidarElevatorSlaveSparkID = 0;

    public static final int kVidarElevatorDoubleSolenoidForwardsID = 0;
    public static final int kVidarElevatorDoubleSolenoidReverseID = 0;

    public static final int kElevatorHFXPort = 1;

    //INTAKE
    public static final int kVidarIntakeMasterDeviceID = 3;
    public static final int kVidarIntakeSlaveDeviceID = 4;
    public static final int kVidarIntakeVictorDeviceID = 5;
    public static final int kVidarAnalogPot = 1;

    //SHOOTER
    public static final int kVidarShooterMasterVictorDeviceID = 0;
    public static final int kVidarShooterSlaveVictorDeviceID = 1;

    //SHOVEL
    public static final int kVidarShovelDeviceID = 0;
    public static final int kVidarShovelSolenoidUpDownID = 0;
    public static final int kVidarShovelHFXPort = 1;
    public static final int kVidarShovelPDPPort = 0;

    //PUSHER
    public static final int kVidarPusherVictorID = 0;
    public static final int kVidarPusherPotID = 0;

    //FINGERS
    public static final int kVidarOpenCloseSolenoidForwardID = 0;
    public static final int kVidarOpenCloseSolenoidReverseID = 1;
    public static final int kVidarExpelSolenoidForwardID = 2;
    public static final int kVidarExpelSolenoidReverseID = 3;

    //PCM 0
    public static final int kVidarIntakeUpDownSolenoidForwardID = 2;
    public static final int kVidarIntakeUpDownSolenoidReverseID = 5;

    //PCM 1
    public static final int kInOutSolenoidA = 0;
    public static final int kInOutSolenoidB = 1;

    public static final int kGyroPort = 7;
}
