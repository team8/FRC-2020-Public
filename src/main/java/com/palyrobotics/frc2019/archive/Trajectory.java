//package com.palyrobotics.frc2019.util.trajectory;
//
//import com.palyrobotics.frc2019.util.MathUtil;
//
///**
// * Implementation of a Trajectory using arrays as the underlying storage mechanism.
// *
// * @author Jared341
// */
//public class Trajectory {
//
//    public static class Pair {
//        public Pair(Trajectory left, Trajectory right) {
//            this.left = left;
//            this.right = right;
//        }
//
//        public Trajectory left;
//        public Trajectory right;
//    }
//
//    public static class Segment {
//
//        public double pos, vel, acc, jerk, heading, dt, x, y;
//
//        public Segment() {
//        }
//
//        public Segment(double pos, double vel, double acc, double jerk, double heading, double dt, double x, double y) {
//            this.pos = pos;
//            this.vel = vel;
//            this.acc = acc;
//            this.jerk = jerk;
//            this.heading = heading;
//            this.dt = dt;
//            this.x = x;
//            this.y = y;
//        }
//
//        public Segment(Segment to_copy) {
//            pos = to_copy.pos;
//            vel = to_copy.vel;
//            acc = to_copy.acc;
//            jerk = to_copy.jerk;
//            heading = to_copy.heading;
//            dt = to_copy.dt;
//            x = to_copy.x;
//            y = to_copy.y;
//        }
//
//        public String toString() {
//            return "pos: " + pos + "; vel: " + vel + "; acc: " + acc + "; jerk: " + jerk + "; heading: " + heading;
//        }
//    }
//
//    private Segment[] mSegments;
//    private boolean mInvertedY = false;
//
//    public Trajectory(int length) {
//        mSegments = new Segment[length];
//        for (int i = 0; i < length; ++i) {
//            mSegments[i] = new Segment();
//        }
//    }
//
//    public Trajectory(Segment[] segments) {
//        mSegments = segments;
//    }
//
//    public void setInvertedY(boolean inverted) {
//        mInvertedY = inverted;
//    }
//
//    public int getNumSegments() {
//        return mSegments.length;
//    }
//
//    public Segment getSegment(int index) {
//        if (index < getNumSegments()) {
//            if (!mInvertedY) {
//                return mSegments[index];
//            } else {
//                Segment segment = new Segment(mSegments[index]);
//                segment.y *= -1.0;
//                segment.heading = MathUtil.boundAngle0to2PiRadians(2 * Math.PI - segment.heading);
//                return segment;
//            }
//        } else {
//            return new Segment();
//        }
//    }
//
//    public void setSegment(int index, Segment segment) {
//        if (index < getNumSegments()) {
//            mSegments[index] = segment;
//        }
//    }
//
//    public void scale(double scaling_factor) {
//        for (int i = 0; i < getNumSegments(); ++i) {
//            mSegments[i].pos *= scaling_factor;
//            mSegments[i].vel *= scaling_factor;
//            mSegments[i].acc *= scaling_factor;
//            mSegments[i].jerk *= scaling_factor;
//        }
//    }
//
//    public void append(Trajectory to_append) {
//        Segment[] temp = new Segment[getNumSegments() + to_append.getNumSegments()];
//
//        for (int i = 0; i < getNumSegments(); ++i) {
//            temp[i] = new Segment(mSegments[i]);
//        }
//        for (int i = 0; i < to_append.getNumSegments(); ++i) {
//            temp[i + getNumSegments()] = new Segment(to_append.getSegment(i));
//        }
//
//        this.mSegments = temp;
//    }
//
//    public Trajectory copy() {
//        Trajectory cloned = new Trajectory(getNumSegments());
//        cloned.mSegments = copySegments(this.mSegments);
//        return cloned;
//    }
//
//    private Segment[] copySegments(Segment[] toCopy) {
//        Segment[] copied = new Segment[toCopy.length];
//        for (int i = 0; i < toCopy.length; ++i) {
//            copied[i] = new Segment(toCopy[i]);
//        }
//        return copied;
//    }
//
//    public String toString() {
//        StringBuilder str = new StringBuilder("Segment\tPos\tVel\tAcc\tJerk\tHeading\n");
//        for (int i = 0; i < getNumSegments(); ++i) {
//            Trajectory.Segment segment = getSegment(i);
//            str.append(i).append("\t");
//            str.append(segment.pos).append("\t");
//            str.append(segment.vel).append("\t");
//            str.append(segment.acc).append("\t");
//            str.append(segment.jerk).append("\t");
//            str.append(segment.heading).append("\t");
//            str.append("\n");
//        }
//        return str.toString();
//    }
//
//    public String toStringProfile() {
//        return toString();
//    }
//
//    public String toStringEuclidean() {
//        StringBuilder str = new StringBuilder("Segment\tx\ty\tHeading\n");
//        for (int i = 0; i < getNumSegments(); ++i) {
//            Trajectory.Segment segment = getSegment(i);
//            str.append(i).append("\t");
//            str.append(segment.x).append("\t");
//            str.append(segment.y).append("\t");
//            str.append(segment.heading).append("\t");
//            str.append("\n");
//        }
//        return str.toString();
//    }
//}
