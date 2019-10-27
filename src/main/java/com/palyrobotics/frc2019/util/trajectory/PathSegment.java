package com.palyrobotics.frc2019.util.trajectory;

/**
 * A PathSegment consists of two Translation2d objects (the start and end points) as well as the speed of the robot.
 *
 * @author Team 254
 */
public class PathSegment {
    protected static final double kEpsilon = 1E-9;

    public static class Sample {
        public final Translation2d translation;
        public final double speed;

        public Sample(Translation2d translation, double speed) {
            this.translation = translation;
            this.speed = speed;
        }
    }

    protected double mSpeed;
    protected Translation2d mStart;
    protected Translation2d mEnd;
    protected Translation2d mStartToEnd; //pre-computed for efficiency
    protected double mLength; //pre-computed for efficiency

    public static class ClosestPointReport {
        public double index; //Index of the point on the path segment (not
        //clamped to [0, 1])
        public double clampedIndex; //As above, but clamped to [0, 1]
        public Translation2d closestPoint; //The result of
        //interpolate(clamped_index)
        public double distance; //The distance from closest_point to the query
        //point
    }

    public PathSegment(Translation2d start, Translation2d end, double speed) {
        mEnd = end;
        mSpeed = speed;
        updateStart(start);
    }

    public void updateStart(Translation2d newStart) {
        mStart = newStart;
        mStartToEnd = mStart.inverse().translateBy(mEnd);
        mLength = mStartToEnd.norm();
    }

    public double getSpeed() {
        return mSpeed;
    }

    public Translation2d getStart() {
        return mStart;
    }

    public Translation2d getEnd() {
        return mEnd;
    }

    public double getLength() {
        return mLength;
    }

    //Index is on [0, 1]
    public Translation2d interpolate(double index) {
        return mStart.interpolate(mEnd, index);
    }

    public double dotProduct(Translation2d other) {
        Translation2d startToOther = mStart.inverse().translateBy(other);
        return mStartToEnd.getX() * startToOther.getX() + mStartToEnd.getY() * startToOther.getY();
    }

    public ClosestPointReport getClosestPoint(Translation2d queryPoint) {
        ClosestPointReport rv = new ClosestPointReport();
        if (mLength > kEpsilon) {
            double dotProduct = dotProduct(queryPoint);
            rv.index = dotProduct / (mLength * mLength);
            rv.clampedIndex = Math.min(1.0, Math.max(0.0, rv.index));
            rv.closestPoint = interpolate(rv.index);
        } else {
            rv.index = rv.clampedIndex = 0.0;
            rv.closestPoint = new Translation2d(mStart);
        }
        rv.distance = rv.closestPoint.inverse().translateBy(queryPoint).norm();
        return rv;
    }
}