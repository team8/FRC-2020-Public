package com.palyrobotics.frc2019.util.trajectory;

/**
 * Represents a 2d pose (rigid transform) containing translational and rotational elements.
 * <p>
 * Inspired by Sophus (https://github.com/strasdat/Sophus/tree/master/sophus)
 */
public class RigidTransform2d implements Interpolable<RigidTransform2d> {
    private final static double kEps = 1E-9;

    // Movement along an arc at constant curvature and velocity.
    // We can use ideas from "differential calculus" to create new RigidTransform2d's from deltas.
    public static class Delta {
        public final double dX;
        public final double dY;
        public final double dTheta;

        public Delta(double dX, double dY, double dTheta) {
            this.dX = dX;
            this.dY = dY;
            this.dTheta = dTheta;
        }
    }

    protected Translation2d translation;
    protected Rotation2d rotation;

    public RigidTransform2d() {
        translation = new Translation2d();
        rotation = new Rotation2d();
    }

    public RigidTransform2d(Translation2d translation, Rotation2d rotation) {
        this.translation = translation;
        this.rotation = rotation;
    }

    public RigidTransform2d(RigidTransform2d other) {
        translation = new Translation2d(other.translation);
        rotation = new Rotation2d(other.rotation);
    }

    public static RigidTransform2d fromTranslation(Translation2d translation) {
        return new RigidTransform2d(translation, new Rotation2d());
    }

    public static RigidTransform2d fromRotation(Rotation2d rotation) {
        return new RigidTransform2d(new Translation2d(), rotation);
    }

    /**
     * Obtain a new RigidTransform2d from a (constant curvature) velocity. See: https://github.com/strasdat/Sophus/blob/master/sophus/se2.hpp
     */
    public static RigidTransform2d fromVelocity(Delta delta) {
        double sinTheta = Math.sin(delta.dTheta);
        double cosTheta = Math.cos(delta.dTheta);
        double s, c;
        if (Math.abs(delta.dTheta) < kEps) {
            s = 1.0 - 1.0 / 6.0 * delta.dTheta * delta.dTheta;
            c = 0.5 * delta.dTheta;
        } else {
            s = sinTheta / delta.dTheta;
            c = (1.0 - cosTheta) / delta.dTheta;
        }
        return new RigidTransform2d(new Translation2d(delta.dX * s - delta.dY * c, delta.dX * c + delta.dY * s), new Rotation2d(cosTheta, sinTheta, false));
    }

    public Translation2d getTranslation() {
        return translation;
    }

    public void setTranslation(Translation2d translation) {
        this.translation = translation;
    }

    public Rotation2d getRotation() {
        return rotation;
    }

    public void setRotation(Rotation2d rotation) {
        this.rotation = rotation;
    }

    /**
     * Transforming this RigidTransform2d means first translating by other.translation and then rotating by other.rotation
     *
     * @param other The other transform.
     * @return This transform * other
     */
    public RigidTransform2d transformBy(RigidTransform2d other) {
        return new RigidTransform2d(translation.translateBy(other.translation.rotateBy(rotation)), rotation.rotateBy(other.rotation));
    }

    /**
     * The inverse of this transform "undoes" the effect of translating by this transform.
     *
     * @return The opposite of this transform.
     */
    public RigidTransform2d inverse() {
        Rotation2d rotationInverted = rotation.inverse();
        return new RigidTransform2d(translation.inverse().rotateBy(rotationInverted), rotationInverted);
    }

    /**
     * Do linear interpolation of this transform (there are more accurate ways using constant curvature, but this is good enough).
     */
    @Override
    public RigidTransform2d interpolate(RigidTransform2d other, double x) {
        if (x <= 0) {
            return new RigidTransform2d(this);
        } else if (x >= 1) {
            return new RigidTransform2d(other);
        }
        return new RigidTransform2d(translation.interpolate(other.translation, x), rotation.interpolate(other.rotation, x));
    }

    @Override
    public String toString() {
        return String.format("T:%s, R:%s", translation.toString(), rotation.toString());
    }
}