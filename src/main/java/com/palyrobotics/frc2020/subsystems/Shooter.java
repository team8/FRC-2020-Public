package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.config.subsystem.ShooterConfig;
import com.palyrobotics.frc2020.util.SparkMaxOutput;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.SmartGains;
import com.revrobotics.SparkMax;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;

import javax.annotation.CheckReturnValue;

//TODO: add in code for other classes and make functional. Also make routine to cause horizontal piston to activate first
public class Shooter extends Subsystem{
    private static Shooter sInstance = new Shooter();

    public static Shooter getInstance() {
        return sInstance;
    }

    private ShooterConfig mConfig = Configs.get(ShooterConfig.class);

    private SparkMaxOutput mOutput = new SparkMaxOutput();

    private DoubleSolenoid.Value mExtend = DoubleSolenoid.Value.kForward,
            mRetract = DoubleSolenoid.Value.kReverse,
            mOff = DoubleSolenoid.Value.kOff;

    private DoubleSolenoid.Value mHorizontalOutput = mRetract;

    private DoubleSolenoid.Value mVerticalOutput = mRetract;



    public enum ShooterState {
        IDLE, SHOOTING
    }

    public enum HoodState {
        LOW, MEDIUM, HIGH
    }

    private ShooterState mState = ShooterState.IDLE;

    private HoodState mHoodState = HoodState.LOW;

    protected Shooter() {
        super("shooter");
    }


    @Override
    public void update(Commands commands, RobotState robotState) {
        //given a wanted shooter state, set the wantedShooterState to something based on that state using a switch
        mState = commands.wantedShooterState;
        mHoodState = commands.wantedHoodState;

        switch(mState){
            case IDLE:
                mOutput.setPercentOutput(0);
                break;
            case SHOOTING:
                //sets up motion profile for the shooter in order to reach a speed.
                mOutput.setTargetSmartVelocity(commands.robotSetPoints.shooterVelocitySetPoint, mConfig.shooterGains);
                break;
        }

        switch(mHoodState){
            case LOW:
                mVerticalOutput = mRetract;
                mHorizontalOutput = mRetract;
                break;
            case MEDIUM:
                mVerticalOutput = mExtend;
                mHorizontalOutput = mExtend;
                break;
            case HIGH:
                mVerticalOutput = mExtend;
                mHorizontalOutput = mRetract;
                break;
        }
    }

    @Override
    public void reset() {
        mOutput.setPercentOutput(0);
        mState = ShooterState.IDLE;
        mHoodState = HoodState.LOW;
        mVerticalOutput = mRetract;
        mHorizontalOutput = mRetract;
    }

    public SparkMaxOutput getOutput() {
        return mOutput;
    }

    public DoubleSolenoid.Value getHorizontalOutput() {
        return mHorizontalOutput;
    }

    public DoubleSolenoid.Value getVerticalOutput() {
        return mVerticalOutput;
    }

    public double feetToMeters (double feet){
        return feet * 0.3048;
    }

    public double metersToFeet(double meters){
        return 3.28084 * meters;
    }

    public double ouncesToKg(double oz){
        return 0.0283495 * oz;
    }

    public double kgToOunces(double kg){
        return kg * 35.274;
    }

    public double degreesToRadians(double degrees){
        return degrees * Math.PI/180;
    }

    public double radiansToDegrees(double degrees){
        return degrees * 180/ Math.PI;
    }


    public double projectedHeight(double initDistance, double initHeight, double tInterval, double initAngle, double initSpeed , double maxTime){
        /* README
            input imperial values, program will convert into metric
            make sure maxTime is a multiple of tInterval
            Function / measurements taken from Mr. Law's spreadsheet.
        */

        //change everything to metric
        initSpeed = feetToMeters(initSpeed); //change to meters/second
        initAngle = degreesToRadians(initAngle); // change to radians from degrees
        initHeight = feetToMeters(initHeight); //change to meters from feet
        initDistance = feetToMeters(initDistance); // change to meters from feet

        double ballDiameter = feetToMeters(7.0 / 12); // in meters, converted from inches
        double ballMass = ouncesToKg(5); //in kg, converted from oz
        double dragCoef = 0.5;
        double densityAir = 1.2; //kg/m^3
        double gravity = 9.80665; //m/s^2
        double ballTopSpin = (-metersToFeet(initSpeed)) * 2/ (Math.PI * (metersToFeet(ballDiameter)))/ 10;
        double dragConst = densityAir * dragCoef * Math.PI * ballDiameter * ballDiameter / 8;

        double dX = initDistance; //meters
        double dY = initHeight; //meters
        double distance = metersToFeet(dX); //feet
        double height = metersToFeet(dY); //feet
        double vX = initSpeed * Math.cos(initAngle);
        double vY = initSpeed * Math.sin(initAngle);
        double v2 = Math.pow(vX, 2) + Math.pow(vY, 2);
        double v2Y = v2 * Math.sin(initAngle);
        double v2X = v2 * Math.cos(initAngle);
        double aX = -v2X * (dragConst / ballMass);
        double aY = (-gravity - (dragConst / ballMass * v2Y)) - (densityAir * Math.pow(ballDiameter,3) * ballTopSpin * Math.sqrt(v2) / ballMass);
        double angle = initAngle;

        for(double i = tInterval; i < maxTime; i += tInterval){
            //starts at tInterval b/c already did 1 iteration
            dX = dX + ((vX * tInterval) + aX * Math.pow(tInterval, 2) / 2);
            dY = dY + ((vY * tInterval) + aY * Math.pow(tInterval, 2) / 2);
            height = metersToFeet(dY);
            distance = metersToFeet(dX);
            vX = vX + aX * tInterval;
            vY = vY + aY * tInterval;
            angle = Math.atan(vY/vX);
            v2 = Math.pow(vX, 2) + Math.pow(vY, 2);
            v2X = v2 * Math.cos(angle);
            v2Y = v2 * Math.sin(angle);
            aX = -(dragConst / ballMass * v2X);
            aY = (-gravity - (dragConst / ballMass * v2Y)) - (densityAir * Math.pow(ballDiameter,3) * ballTopSpin * Math.sqrt(v2) / ballMass);
        }
        return height;
    }

}
