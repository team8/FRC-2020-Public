package com.palyrobotics.frc2019.behavior.routines.elevator;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.subsystems.Elevator;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;

import java.util.ArrayList;
import java.util.Collections;

public class ElevatorMeasureSpeedAtOutputRoutine extends Routine {

    private final double mPercentOutput, mFF, mEncoderCutoff;
    private ArrayList<Double> mVelocityMeasurements = new ArrayList<>(200);

    public ElevatorMeasureSpeedAtOutputRoutine(double percentOutput, double ff, double encoderCutoff) {
        mPercentOutput = percentOutput;
        mFF = ff;
        mEncoderCutoff = encoderCutoff;
    }

    @Override
    public void start() {
        System.out.println("Starting to measure speed...");
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedElevatorState = Elevator.ElevatorState.PERCENT_OUTPUT;
        commands.customElevatorPercentOutput = mPercentOutput + mFF;
        mVelocityMeasurements.add(RobotState.getInstance().elevatorVelocity);
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        commands.wantedElevatorState = Elevator.ElevatorState.IDLE;
        Collections.sort(mVelocityMeasurements);
        double medianVelocity = mVelocityMeasurements.get(mVelocityMeasurements.size() / 2);
        System.out.printf("At Percent Output %f Median velocity inch/s: %f (with feed-forward %f)%n", mPercentOutput, medianVelocity, mFF);
        CSVWriter.addData("measureVelInchPerSec", mPercentOutput, medianVelocity);
        return commands;
    }

    @Override
    public boolean finished() {
        return RobotState.getInstance().elevatorPosition > mEncoderCutoff;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{elevator};
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
