package com.palyrobotics.frc2019.util.trajectory;

import java.text.DecimalFormat;

/**
 * A translation in a 2d coordinate frame. Translations are simply shifts in an (x, y) plane.
 *
 * @author Team 254
 */
public class Translation2d implements Interpolable<Translation2d> {
    protected double x, y;

    public Translation2d() {
        x = 0;
        y = 0;
    }

    public Translation2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Translation2d(Translation2d other) {
        x = other.x;
        y = other.y;
    }

    /**
     * The "norm" of a transform is the Euclidean distance in x and y.
     *
     * @return Sqrt(x ^ 2 + y ^ 2)
     */
    public double norm() {
        return Math.hypot(x, y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    /**
     * We can compose Translation2d's by adding together the x and y shifts.
     *
     * @param other The other translation to add.
     * @return The combined effect of translating by this object and the other.
     */
    public Translation2d translateBy(Translation2d other) {
        return new Translation2d(x + other.x, y + other.y);
    }

    /**
     * We can also rotate Translation2d's. See: https://en.wikipedia.org/wiki/Rotation_matrix
     *
     * @param rotation The rotation to apply.
     * @return This translation rotated by rotation.
     */
    public Translation2d rotateBy(Rotation2d rotation) {
        return new Translation2d(x * rotation.cos() - y * rotation.sin(), x * rotation.sin() + y * rotation.cos());
    }

    /**
     * The inverse simply means a Translation2d that "undoes" this object.
     *
     * @return Translation by -x and -y.
     */
    public Translation2d inverse() {
        return new Translation2d(-x, -y);
    }

    @Override
    public Translation2d interpolate(Translation2d other, double x) {
        if (x <= 0) {
            return new Translation2d(this);
        } else if (x >= 1) {
            return new Translation2d(other);
        }
        return extrapolate(other, x);
    }

    public Translation2d extrapolate(Translation2d other, double x) {
        return new Translation2d(x * (other.x - this.x) + this.x, x * (other.y - y) + y);
    }

    @Override
    public String toString() {
        var format = new DecimalFormat("#0.000");
        return String.format("(%s, %s)", format.format(x), format.format(y));
    }
}
