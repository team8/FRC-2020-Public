package com.palyrobotics.frc2020.util.control;

import com.palyrobotics.frc2020.config.RobotConfig;
import com.palyrobotics.frc2020.util.config.Configs;
import edu.wpi.first.wpilibj.DriverStation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class SimpleControllerBase {

    private static Map<ControllerOutput.Mode, Integer> sModeToSlot = Map.of(
            ControllerOutput.Mode.PROFILED_POSITION, 1,
            ControllerOutput.Mode.PROFILED_VELOCITY, 2
    );

    protected RobotConfig mRobotConfig = Configs.get(RobotConfig.class);

    private double mLastReference, mLastArbitraryPercentOutput;
    private int mLastSlot;
    private ControllerOutput.Mode mLastMode;
    private Map<Integer, Gains> mLastGains = new HashMap<>();

    public boolean setOutput(ControllerOutput output) {
        ControllerOutput.Mode mode = output.getControlMode();
        // Checks to make sure we are using this properly
        boolean isSmart = mode == ControllerOutput.Mode.PROFILED_POSITION || mode == ControllerOutput.Mode.PROFILED_VELOCITY,
                requiresGains = isSmart || mode == ControllerOutput.Mode.POSITION || mode == ControllerOutput.Mode.VELOCITY;
        Gains gains = output.getGains();
        // Slot is determined based on control mode
        // TODO add feature to add custom slots
        int slot = sModeToSlot.getOrDefault(mode, 0);
        updateGainsIfChanged(gains, slot);
        boolean areGainsEqual = !requiresGains || Objects.equals(gains, mLastGains.get(slot));
        double reference = output.getReference(), arbitraryPercentOutput = output.getArbitraryDemand();
        if (!areGainsEqual || slot != mLastSlot || mode != mLastMode || reference != mLastReference || arbitraryPercentOutput != mLastArbitraryPercentOutput) {
            if (setReference(mode, slot, reference, arbitraryPercentOutput)) {
                mLastSlot = slot;
                mLastMode = mode;
                mLastReference = reference;
                mLastArbitraryPercentOutput = arbitraryPercentOutput;
                if (!areGainsEqual) { // Special check since the copy function creates garbage and should only be done when necessary. All other variables are trivial to set.
                    mLastGains.put(slot, Configs.copy(gains));
                }
                return true;
//                System.out.printf("%s, %d, %f, %f, %s%n", type, slot, reference, arbitraryPercentOutput, Configs.toJson(gains));
            } else {
                DriverStation.reportError(String.format("Error updating output on spark max with ID: %d", getId()), new RuntimeException().getStackTrace());
            }
        }
        return false;
    }

    protected void updateGainsIfChanged(Gains gains, int slot) {
        if (gains != null) {
            boolean isFirstInitialization = !mLastGains.containsKey(slot);
            if (isFirstInitialization) { // Empty gains for default value instead of null
                mLastGains.put(slot, (slot == 1 || slot == 2) ? new SmartGains() : new Gains()); // TODO a little ugly
            }
            Gains lastGains = mLastGains.get(slot);
            updateGains(isFirstInitialization, slot, gains, lastGains);
        }
    }

    protected void updateGains(boolean isFirstInitialization, int slot, Gains newGains, Gains lastGains) {
        if (Double.compare(lastGains.p, newGains.p) != 0) setP(slot, newGains.p);
        if (Double.compare(lastGains.i, newGains.i) != 0) setI(slot, newGains.i);
        if (Double.compare(lastGains.d, newGains.d) != 0) setD(slot, newGains.d);
        if (Double.compare(lastGains.f, newGains.f) != 0) setF(slot, newGains.f);
        if (Double.compare(lastGains.iZone, newGains.iZone) != 0) setIZone(slot, newGains.iZone);
    }

    abstract int getId();

    abstract boolean setReference(ControllerOutput.Mode mode, int slot, double reference, double arbitraryPercentOutput);

    abstract void setP(int slot, double p);

    abstract void setI(int slot, double i);

    abstract void setIZone(int slot, double iZone);

    abstract void setD(int slot, double d);

    abstract void setF(int slot, double f);
}
