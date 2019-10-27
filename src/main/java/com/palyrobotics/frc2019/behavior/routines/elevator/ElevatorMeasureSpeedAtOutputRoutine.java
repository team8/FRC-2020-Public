package com.palyrobotics.frc2019.behavior.routines.elevator;

import com.palyrobotics.frc2019.behavior.Routine;
import com.palyrobotics.frc2019.config.Commands;
import com.palyrobotics.frc2019.config.RobotState;
import com.palyrobotics.frc2019.subsystems.Elevator;
import com.palyrobotics.frc2019.subsystems.Subsystem;
import com.palyrobotics.frc2019.util.csvlogger.CSVWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ElevatorMeasureSpeedAtOutputRoutine extends Routine {

    private final double mPercentOutput, mArbitraryPercentOutput, mEncoderCutoff;
    private ArrayList<Double> mVelocityMeasurements = new ArrayList<>(200);

    public ElevatorMeasureSpeedAtOutputRoutine(double percentOutput, double arbitraryPercentOutput, double encoderCutoff) {
        mPercentOutput = percentOutput;
        mArbitraryPercentOutput = arbitraryPercentOutput;
        mEncoderCutoff = encoderCutoff;
    }

    @Override
    public void start() {
        System.out.println("Starting to measure speed...");
    }

    @Override
    public Commands update(Commands commands) {
        commands.wantedElevatorState = Elevator.ElevatorState.PERCENT_OUTPUT;
        commands.customElevatorPercentOutput = mPercentOutput + mArbitraryPercentOutput;
        mVelocityMeasurements.add(RobotState.getInstance().elevatorVelocity);
        return commands;
    }

    @Override
    public Commands cancel(Commands commands) {
        commands.wantedElevatorState = Elevator.ElevatorState.IDLE;
        Collections.sort(mVelocityMeasurements);
        double medianVelocity = mVelocityMeasurements.get(mVelocityMeasurements.size() / 2);
        String output = String.format("At Percent Output %f Median velocity inch/s: %f (with feed-forward %f)%n", mPercentOutput, medianVelocity, mArbitraryPercentOutput);
        String separator = Stream.generate(() -> "/").limit(output.length()).collect(Collectors.joining());
        System.out.println(separator);
        System.out.println(output);
        System.out.println(separator);
        CSVWriter.addData("measureVelInchPerSec", mPercentOutput, medianVelocity);
        return commands;
    }

    @Override
    public boolean isFinished() {
        return RobotState.getInstance().elevatorPosition > mEncoderCutoff;
    }

    @Override
    public Subsystem[] getRequiredSubsystems() {
        return new Subsystem[]{mElevator};
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }
}
