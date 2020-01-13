package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.config.subsystem.ShooterConfig;
import com.palyrobotics.frc2020.util.SparkMaxOutput;
import com.palyrobotics.frc2020.util.config.Configs;
import com.palyrobotics.frc2020.util.control.SmartGains;
import com.revrobotics.SparkMax;
import edu.wpi.first.wpilibj.Spark;

public class Shooter extends Subsystem{
    private static Shooter sInstance = new Shooter();

    public static Shooter getInstance() {
        return sInstance;
    }

    private ShooterConfig mConfig = Configs.get(ShooterConfig.class);

    private SparkMaxOutput mOutput = new SparkMaxOutput();

    public enum ShooterState {
        IDLE, SHOOTING
    }

    private ShooterState mState = ShooterState.IDLE;

    protected Shooter() {
        super("shooter");
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
        //make sure maxTime is a multiple of tInterval
        //outputs in feet
        //todo: create comments explaining each variable

        //change everything to metric
        initSpeed = feetToMeters(initSpeed);
        initAngle = degreesToRadians(initAngle);
        initHeight = feetToMeters(initHeight);
        initDistance = feetToMeters(initDistance);

        double ballDiameter = feetToMeters(7.0 / 12); // in meters
        double ballMass = ouncesToKg(5); //in kg
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
            //starts at 1 b/c already did 1 iteration
            dX = dX + ((vX * tInterval) + aX * Math.pow(tInterval, 2) / 2);
            dY = dY + ((vY * tInterval) + aY * Math.pow(tInterval, 2) / 2);
            height = metersToFeet(dY); //feet
            distance = metersToFeet(dX); //feet
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

    @Override
    public void update(Commands commands, RobotState robotState) {
        //given a wanted shooter state, set the wantedShooterState to something based on that state using a switch
        mState = commands.wantedShooterState;
        switch(mState){
            case IDLE:
                mOutput.setPercentOutput(0);
                break;
            case SHOOTING:
                //sets up motion profile for the shooter in order to reach a speed.
                mOutput.setTargetSmartVelocity(commands.robotSetPoints.shooterVelocitySetPoint, mConfig.shooterGains);
                break;
        }
    }

    @Override
    public void reset() {
        mOutput.setPercentOutput(0);
        mState = ShooterState.IDLE;
    }

    public SparkMaxOutput getOutput() {
        return mOutput;
    }
}
