//package com.palyrobotics.frc2019.util;
//
//import edu.wpi.first.wpilibj.Joystick;
//
///**
// * Class to store Joystick input
// *
// * @author Nihar
// */
//public class JoystickInput {
//
//    private double mX, mY;
//    private boolean[] mButtons = new boolean[12];
//
//    @Override
//    public String toString() {
//        return String.format("Joystick X: %s Y: %s", mX, mY);
//    }
//
//    public void update(Joystick joystick) {
//        mX = joystick.getX();
//        mY = joystick.getY();
//        for (int i = 1; i < 12; i++) {
//            //getRawButton(1) is the trigger
//            mButtons[i] = joystick.getRawButton(i);
//        }
//    }
//
//    public double getX() {
//        return mX;
//    }
//
//    public double getY() {
//        return mY;
//    }
//
//    public boolean getButtonPressed(int button) {
//        return mButtons[button];
//    }
//
//    public boolean getTriggerPressed() {
//        return mButtons[1];
//    }
//}
