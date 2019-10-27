package com.palyrobotics.frc2019.util.input;

import java.util.HashMap;
import java.util.Map;

public class XboxController extends edu.wpi.first.wpilibj.XboxController {

    private static final double kTriggerThreshold = 0.8;

    public XboxController(int port) {
        super(port);
    }

    private int mLastPOV = -1;
    private Map<Hand, Boolean> mLastTriggers = new HashMap<>(Map.of(
            Hand.kLeft, false,
            Hand.kRight, false
    ));

    private boolean isTriggerDown(Hand hand) {
        return getTriggerAxis(hand) > kTriggerThreshold;
    }

    public void updateLastInputs() {
        mLastPOV = getPOV();
        mLastTriggers.replaceAll((hand, b) -> isTriggerDown(hand));
    }

    public boolean getDPadRightPressed() {
        return getPOV() != mLastPOV && getDPadRight();
    }

    public boolean getDPadUpPressed() {
        return getPOV() != mLastPOV && getDPadUp();
    }

    public boolean getDPadDownPressed() {
        return getPOV() != mLastPOV && getDPadDown();
    }

    public boolean getDPadLeftPressed() {
        return getPOV() != mLastPOV && getDPadLeft();
    }

    public boolean getDPadRight() {
        return getPOV() == 90;
    }

    public boolean getDPadUp() {
        return getPOV() == 0;
    }

    public boolean getDPadDown() {
        return getPOV() == 180;
    }

    public boolean getDPadLeft() {
        return getPOV() == 270;
    }

    public boolean getTriggerPressed(Hand hand) {
        return mLastTriggers.get(hand) != getTriggerAxis(hand) > kTriggerThreshold && getTriggerAxis(hand) > kTriggerThreshold;
    }

    public boolean getRightTriggerPressed() {
        return getTriggerPressed(Hand.kRight);
    }

    public boolean getLeftTriggerPressed() {
        return getTriggerPressed(Hand.kLeft);
    }

    public boolean getLeftBumperPressed() {
        return getBumperPressed(Hand.kLeft);
    }

    public boolean getRightBumperPressed() {
        return getBumperPressed(Hand.kRight);
    }

    public void setRumble(boolean on) {
        setRumble(RumbleType.kRightRumble, on ? 1.0 : 0.0);
        setRumble(RumbleType.kLeftRumble, on ? 1.0 : 0.0);
    }

    public boolean getRightTrigger() {
        return isTriggerDown(Hand.kRight);
    }

    public boolean getLeftTrigger() {
        return isTriggerDown(Hand.kLeft);
    }
}
