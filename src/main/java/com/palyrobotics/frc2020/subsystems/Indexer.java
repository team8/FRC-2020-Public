package com.palyrobotics.frc2020.subsystems;

import com.palyrobotics.frc2020.config.Commands;
import com.palyrobotics.frc2020.config.RobotState;
import com.palyrobotics.frc2020.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2020.util.SparkMaxOutput;
import com.palyrobotics.frc2020.util.config.Configs;
import edu.wpi.first.wpilibj.Spark;

public class Indexer extends Subsystem {
    
    private static Indexer sInstance = new Indexer();
    private IndexerConfig mConfig = Configs.get(IndexerConfig.class);
    private SparkMaxOutput mOutput;
    private IndexerState mState;

    public Indexer() {
        super("indexer");
    }

    public enum IndexerState {
        IDLE, MOVING
    }

    public static Indexer getInstance() {
        return sInstance;
    }

    public void start() {
        mState = IndexerState.IDLE;
        mOutput = new SparkMaxOutput();
    }

    public void reset() {
        mState = IndexerState.IDLE;
        mOutput = new SparkMaxOutput();
    }

    public void stop() {
        mState = IndexerState.IDLE;
        mOutput = new SparkMaxOutput();
    }

    @Override
    public void update(Commands commands, RobotState robotState) {
        mState = commands.wantedIndexerState;
        switch (mState) {
            case IDLE:
                mOutput.setPercentOutput(0);
            case MOVING:
                mOutput.setTargetSmartVelocity(mConfig.transferVelocity, mConfig.gains);
        }
    }

    public SparkMaxOutput getOutput() {
        return mOutput;
    }

}
