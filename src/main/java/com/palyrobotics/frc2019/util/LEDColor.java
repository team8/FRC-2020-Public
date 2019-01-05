package com.palyrobotics.frc2018.util;

public abstract class LEDColor {
    public enum Color{
        RED, ORANGE, YELLOW, GREEN, BLUE, GREEN_DARK, RAINBOW, BLACK, OFF
    }

    private static Color mColor = Color.BLACK;

    public static double getValue(Color color) {
        switch(color) {
            case RED:
                return 0.61;
            case ORANGE:
                return 0.65;
            case YELLOW:
                return 0.69;
            case GREEN:
                return 0.77;
            case GREEN_DARK:
                return 0.75;
            case BLUE:
                return 0.87;
            case RAINBOW: // beautiful xd
                return -0.99;
            case BLACK:
                return 0.99;
            case OFF:
                return 0;
        }
        return 0;
    }

    public static Color getColor() {
        return mColor;
    }

    public static void setColor(Color color) {
        mColor = color;
    }
}
