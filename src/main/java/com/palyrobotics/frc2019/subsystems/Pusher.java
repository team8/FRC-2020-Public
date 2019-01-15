package com.palyrobotics.frc2019.subsystems;

import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.Constants;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.robot.HardwareAdapter;
import com.palyrobotics.frc2019.util.logger.LeveledString;
import edu.wpi.first.wpilibj.Solenoid;
public class Pusher extends Subsystem {

    private static Pusher instance = new Pusher();

    public static Pusher getInstance() { return instance; }

    public static void resetInstance() { instance = new Pusher();}

    private boolean mInOutOutput= false;

    public enum PusherState {
        IN, OUT
    }

    private PusherState mState = PusherState.IN;

    protected Pusher(){
        super("Pusher");
    }

    @Override
    public void start(){
        mState = PusherState.IN;
    }

    @Override
    public void stop(){
        mState = PusherState.IN;
    }

    @Override
    public void update(Commands commands, RobotState robotState){
        mState = commands.wantedPusherInOutState;

        switch(mState) {
            case IN:
                mInOutOutput = false;
            case OUT:
                mInOutOutput = true;
        }
    }

    public PusherState getPusherState() {
        return mState;
    }
    public boolean getInOutOutput() {
        return mInOutOutput;
    }
    @Override
    public String getStatus() {
        return "Pusher State: " + mState + "\nIn Out output" + mInOutOutput ;
    }
}
