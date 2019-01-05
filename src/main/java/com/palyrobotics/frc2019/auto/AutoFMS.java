package com.palyrobotics.frc2018.auto;

import com.palyrobotics.frc2018.util.logger.Logger;
import edu.wpi.first.wpilibj.DriverStation;

import java.util.logging.Level;

public class AutoFMS {

    public enum Side {
        LEFT,
        RIGHT,
        NONE
    }

    protected AutoFMS() {

    }

    private static AutoFMS instance_ = new AutoFMS();

    public static AutoFMS getInstance() {
        return instance_;
    }

    /*
    For Unit Tests
     */
    private Side switchSide;
    private Side scaleSide;

    public void setSwitch(Side side) {
        this.switchSide = side;
    }

    public void setScale(Side side) {
        this.scaleSide = side;
    }

    public Side getSwitchSide() {
        String dataString;
        try {
            dataString = DriverStation.getInstance().getGameSpecificMessage();
        } catch(UnsatisfiedLinkError error) {
            return this.switchSide;
        } catch(NoClassDefFoundError error) {
            return this.switchSide;
        }

        if(dataString.length() == 0) {
            return Side.NONE;
        }

        if(String.valueOf(dataString.charAt(0)).equals("L")) {
            return Side.LEFT;
        } else if(String.valueOf(dataString.charAt(0)).equals("R")) {
            return Side.RIGHT;
        } else {
            Logger.getInstance().logRobotThread(Level.SEVERE, "Failed to receive switch side from FMS!");
            return null;
        }
    }

    public Side getScaleSide() {
        String dataString;
        try {
            dataString = DriverStation.getInstance().getGameSpecificMessage();
        } catch(UnsatisfiedLinkError error) {
            return this.scaleSide;
        } catch(NoClassDefFoundError error) {
            return this.scaleSide;
        }
        
        if(dataString.length() == 0) {
            return Side.NONE;
        }
        if(String.valueOf(dataString.charAt(1)).equals("L")) {
            return Side.LEFT;
        } else if(String.valueOf(dataString.charAt(1)).equals("R")) {
            return Side.RIGHT;
        } else {
            Logger.getInstance().logRobotThread(Level.SEVERE, "Failed to receive scale side from FMS!");
            return null;
        }
    }

    public static boolean isFMSDataAvailable() {
        return AutoFMS.getInstance().getScaleSide() != Side.NONE && AutoFMS.getInstance().getSwitchSide() != Side.NONE;
    }
}

