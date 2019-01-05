package com.palyrobotics.frc2018.util;

public class XboxInput extends JoystickInput {
    public double leftX, leftY, rightX, rightY, leftTrigger, rightTrigger;
    public boolean leftTriggerPressed, rightTriggerPressed, buttonA, buttonB, buttonX, buttonY, buttonStart, buttonBack, dPadUp, dPadRight, dPadDown, dPadLeft, rightBumper, leftBumper;

    public void update(XboxController x) {
        this.leftX = x.getLeftX();
        this.leftY = x.getLeftY();
        this.rightX = x.getRightX();
        this.rightY = x.getRightY();
        this.leftTrigger = x.getLeftTrigger();
        this.rightTrigger = x.getRightTrigger();
        this.leftTriggerPressed = x.getLeftTriggerPressed();
        this.rightTriggerPressed = x.getRightTriggerPressed();
        this.buttonA = x.getButtonA();
        this.buttonB = x.getButtonB();
        this.buttonX = x.getButtonX();
        this.buttonY = x.getButtonY();
        this.buttonStart = x.getButtonStart();
        this.buttonBack = x.getButtonBack();
        this.dPadUp = x.getDpadUp();
        this.dPadRight = x.getDpadRight();
        this.dPadDown = x.getDpadDown();
        this.dPadLeft = x.getDpadLeft();
        this.rightBumper = x.getRightBumper();
        this.leftBumper = x.getLeftBumper();
    }

    public double getLeftX() {
        return leftX;
    }

    public double getLeftY() {
        return leftY;
    }

    public double getRightX() {
        return rightX;
    }

    public double getRightY() {
        return rightY;
    }

    public double getLeftTrigger() {
        return leftTrigger;
    }

    public double getRightTrigger() {
        return rightTrigger;
    }

    public boolean getLeftTriggerPressed() {
        return leftTriggerPressed;
    }

    public boolean getRightTriggerPressed() {
        return rightTriggerPressed;
    }

    public boolean getButtonA() {
        return buttonA;
    }

    public boolean getButtonB() {
        return buttonB;
    }

    public boolean getButtonX() {
        return buttonX;
    }

    public boolean getButtonY() {
        return buttonY;
    }

    public boolean getButtonStart() {
        return buttonStart;
    }

    public boolean getButtonBack() {
        return buttonBack;
    }

    public boolean getdPadUp() {
        return dPadUp;
    }

    public boolean getdPadRight() {
        return dPadRight;
    }

    public boolean getdPadDown() {
        return dPadDown;
    }

    public boolean getdPadLeft() {
        return dPadLeft;
    }

    public boolean getRightBumper() {
        return rightBumper;
    }

    public boolean getLeftBumper() {
        return leftBumper;
    }


}