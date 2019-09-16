package com.palyrobotics.frc2019.util.trajectory;

import com.palyrobotics.frc2019.config.Constants.DrivetrainConstants;

import java.util.*;

/**
 * A Path is a recording of the path that the robot takes. Path objects consist of a List of Waypoints that the robot passes by. Using multiple Waypoints in a
 * Path object and the robot's current speed, the code can extrapolate future Waypoints and predict the robot's motion. It can also dictate the robot's motion
 * along the set path.
 * <p>
 * Various changes to path construction were made in accordance with team 1712's whitepaper: https://www.chiefdelphi.com/media/papers/3488?
 * These improvements are represented by the following methods:
 * // TODO fix these, they do not do anything right now
 * {@link #injectPoints(List)} injectPoints}
 * {@link #smoothOut(List) smoothen}
 * {@link #setVelocities(List) setVelocities}
 * {@link #getCurvature(Translation2d, Translation2d, Translation2d) getCurvature}
 *
 * @author Team 254, Calvin Yan
 */
public class Path {
    protected static final double kSegmentCompletePercentage = .9;

    protected List<Waypoint> mWaypoints;
    protected List<PathSegment> mSegments;
    protected Set<String> mMarkersCrossed;

    /**
     * A point along the Path, which consists of the location, the speed, and a string marker (that future code can identify). Paths consist of a List of
     * Waypoints.
     */
    public static class Waypoint {
        public final Translation2d position;
        public final double speed;
        public final Optional<String> marker;
        public final boolean isRelative;

        public Waypoint(Translation2d position) {
            this.position = position;
            this.speed = -1;
            this.marker = Optional.empty();
            this.isRelative = false;
        }

        public Waypoint(Translation2d position, double speed) {
            this.position = position;
            this.speed = speed;
            this.marker = Optional.empty();
            this.isRelative = false;
        }

        public Waypoint(Translation2d position, double speed, boolean isRelative) {
            this.position = position;
            this.speed = speed;
            this.marker = Optional.empty();
            this.isRelative = isRelative;
        }

        public Waypoint(Translation2d position, double speed, String marker) {
            this.position = position;
            this.speed = speed;
            this.marker = Optional.of(marker);
            this.isRelative = false;
        }

        public Waypoint(Translation2d position, double speed, String marker, boolean isRelative) {
            this.position = position;
            this.speed = speed;
            this.marker = Optional.of(marker);
            this.isRelative = isRelative;
        }
    }

    public Path(List<Waypoint> wayPoints) {
        mMarkersCrossed = new HashSet<>();
        injectPoints(wayPoints);
        smoothOut(wayPoints);
        setVelocities(wayPoints);
        mWaypoints = wayPoints;
        mSegments = new ArrayList<>();
        for (int i = 0; i < wayPoints.size() - 1; ++i) {
            mSegments.add(new PathSegment(wayPoints.get(i).position, wayPoints.get(i + 1).position, wayPoints.get(i).speed));
        }
        //The first waypoint is already complete
        //If 1, assume sticking a point before this and this point needs to stay
        if (mWaypoints.size() > 1) {
            Waypoint first_waypoint = mWaypoints.get(0);
			first_waypoint.marker.ifPresent(s -> mMarkersCrossed.add(s));
            mWaypoints.remove(0);
        }
    }

    /**
     * @param position initial position
     * @return Returns the distance from the position to the first point on the path
     */
    public double update(Translation2d position) {
        double rv = 0.0;
        for (Iterator<PathSegment> it = mSegments.iterator(); it.hasNext(); ) {
            PathSegment segment = it.next();
            PathSegment.ClosestPointReport closest_point_report = segment.getClosestPoint(position);
            if (closest_point_report.index >= kSegmentCompletePercentage) {
                it.remove();
                if (mWaypoints.size() > 0) {
                    Waypoint waypoint = mWaypoints.get(0);
					waypoint.marker.ifPresent(s -> mMarkersCrossed.add(s));
                    mWaypoints.remove(0);
                }
            } else {
                if (closest_point_report.index > 0.0) {
                    //Can shorten this segment
                    segment.updateStart(closest_point_report.closest_point);
                }
                //We are done
                rv = closest_point_report.distance;
                //...unless the next segment is closer now
                if (it.hasNext()) {
                    PathSegment next = it.next();
                    PathSegment.ClosestPointReport next_closest_point_report = next.getClosestPoint(position);
                    if (next_closest_point_report.index > 0 && next_closest_point_report.index < kSegmentCompletePercentage
                            && next_closest_point_report.distance < rv) {
                        next.updateStart(next_closest_point_report.closest_point);
                        rv = next_closest_point_report.distance;
                        mSegments.remove(0);
                        if (mWaypoints.size() > 0) {
                            Waypoint waypoint = mWaypoints.get(0);
							waypoint.marker.ifPresent(s -> mMarkersCrossed.add(s));
                            mWaypoints.remove(0);
                        }
                    }
                }
                break;
            }
        }
        return rv;
    }

    // Add intermediate, closely-spaced points between wayPoints to improve following
    private void injectPoints(List<Waypoint> wayPoints) {
        // Path to insert points into
        ArrayList<Waypoint> newPoints = new ArrayList<>(wayPoints);

        int index = 0; // Keep track of the index in newPoints to insert points into

        for (int i = 0; i < wayPoints.size() - 1; i++) {
            Translation2d start = wayPoints.get(i).position;
            Translation2d end = wayPoints.get(i + 1).position;
            double length = Math.sqrt(Math.pow(start.getX() - end.getX(), 2) + Math.pow(start.getY() - end.getY(), 2));
            // Portion of the segment's length occupied by one interval; needed for the interpolate() function
            double interpolateX = DrivetrainConstants.kInsertionSpacingInches / length;
            // Insert points on the segment using interpolation
            for (double j = interpolateX; j < 1; j += interpolateX) {
                newPoints.add(index++, new Waypoint(start.interpolate(end, j), wayPoints.get(i + 1).speed));
            }
            // Increment pointer so it inserts between the next pair of wayPoints
            index++;
        }
        wayPoints = newPoints;
    }

    // Smooth the path using gradient descent; inspired by https://github.com/KHEngineering/SmoothPathPlanner/blob/11059aa2ec314ba20b364aeea3c968aca2672b49/src/usfirst/frc/team2168/robot/FalconPathPlanner.java#L214
    private void smoothOut(List<Waypoint> wayPoints) {
        ArrayList<Waypoint> oldPts = new ArrayList<>(wayPoints);
        ArrayList<Waypoint> newPts = new ArrayList<>(wayPoints);
        // Sum of the horizontal and vertical shifts of each point in one iteration of smoothing
        double change = DrivetrainConstants.kSmoothingTolerance;
        int numIters = 0;
        // Perform smoothing passes until convergence
        while (change >= DrivetrainConstants.kSmoothingTolerance && numIters <= DrivetrainConstants.kSmoothingMaxIters) {
            change = 0.0;
            // Smooth all points except for the final one
            // We deleted the first waypoint at (0, 0) but for smoothing code to work we have to pretend it's still there
            for (int i = 0; i < oldPts.size() - 1; i++) {
                double x1 = (i > 0) ? newPts.get(i - 1).position.getX() : 0;
                double y1 = (i > 0) ? newPts.get(i - 1).position.getY() : 0;
                double x2 = newPts.get(i).position.getX();
                double y2 = newPts.get(i).position.getY();
                double x3 = newPts.get(i + 1).position.getX();
                double y3 = newPts.get(i + 1).position.getY();
                double x4 = oldPts.get(i).position.getX();
                double y4 = oldPts.get(i).position.getY();

                double changeX = DrivetrainConstants.kSmoothingWeightData * (x4 - x2) + DrivetrainConstants.kSmoothingWeight * (x1 + x3 - (2.0 * x2));
                double changeY = DrivetrainConstants.kSmoothingWeightData * (y4 - y2) + DrivetrainConstants.kSmoothingWeight * (y1 + y3 - (2.0 * y2));
                x2 += changeX;
                y2 += changeY;
                newPts.set(i, new Waypoint(new Translation2d(x2, y2), oldPts.get(i).speed));
                change += Math.abs(changeX + changeY);
            }
            numIters++;
        }

        wayPoints = newPts;
    }

    /*
     * Automatically calculate target velocities throughout the path. For each waypoint before the last one,
     * the velocity is the smaller value between Constants.kPathFollowingMaxVel and k/curvature, where k is an arbitrary constant.
     */

    public void setVelocities(List<Waypoint> wayPoints) {
        for (int i = 0; i < wayPoints.size() - 1; i++) {
            // Check if velocity has not already been set
            if (wayPoints.get(i).speed == -1) {
                Translation2d previous = (i == 0) ? new Translation2d(0, 0) : wayPoints.get(i - 1).position;
                Translation2d current = wayPoints.get(i).position;
                Translation2d next = wayPoints.get(i + 1).position;
                double maxVel = Math.min(DrivetrainConstants.kPathFollowingMaxVel, DrivetrainConstants.kTurnVelocityReduction / getCurvature(previous, current, next));
                wayPoints.set(i, new Waypoint(current, maxVel));
            }
        }
        // Set last waypoint speed to 0
        Translation2d endPt = wayPoints.get(wayPoints.size() - 1).position;
        wayPoints.set(wayPoints.size() - 1, new Waypoint(endPt, 0));
    }

    /*
     * Determine curvature of the circular arc connecting the current waypoint with those directly before and after it
     * Used for curvature calculation and adjustments to velocity around turns
     *
     * @param previous
     *            - the first waypoint in the path to be connected
     * @param current
     *            - the second waypoint to be connected. The curvature calculation applies to this point
     * @param next
     * 			  - the third waypoint in the path to be connected
     * @return - A circular arc representing the path, null if the arc is degenerate
     */
    private static double getCurvature(Translation2d previous, Translation2d current, Translation2d next) {
        double x1 = previous.getX();
        double y1 = previous.getY();
        double x2 = current.getX();
        double y2 = current.getY();
        double x3 = next.getX();
        double y3 = next.getY();

        // Avoid a divide by zero error
        if (x1 == x2) x1 += .001;
        double k1 = 0.5 * (x1 * x1 + y1 * y1 - x2 * x2 - y2 * y2) / (x1 - x2);
        double k2 = (y1 - y2) / (x1 - x2);
        double b = 0.5 * (x2 * x2 - 2 * x2 * k1 + y2 * y2 - x3 * x3 + 2 * x3 * k1 - y3 * y3) / (x3 * k2 - y3 + y2 - x2 * k2);
        double a = k1 - k2 * b;

        double radius = Math.sqrt(Math.pow(x1 - a, 2) + Math.pow(y1 - b, 2));
        if (Double.isNaN(radius)) return 0;
        return 1 / radius;

    }

    public Set<String> getMarkersCrossed() {
        return mMarkersCrossed;
    }

    public double getRemainingLength() {
        double length = 0.0;
		for (PathSegment mSegment : mSegments) {
			length += mSegment.getLength();
		}
        return length;
    }

    /**
     * The robot's current position
     *
     * @param lookahead_distance, A specified distance to predict a future waypoint
     * @return A segment of the robot's predicted motion with start/end points and speed.
     */
    public PathSegment.Sample getLookaheadPoint(Translation2d position, double lookahead_distance) {
        if (mSegments.size() == 0) {
            return new PathSegment.Sample(new Translation2d(), 0);
        }

        //Check the distances to the start and end of each segment. As soon as
        //we find a point > lookahead_distance away, we know the right point
        //lies somewhere on that segment.
        Translation2d position_inverse = position.inverse();
        if (position_inverse.translateBy(mSegments.get(0).getStart()).norm() >= lookahead_distance) {
            //Special case: Before the first point, so just return the first
            //point.
            return new PathSegment.Sample(mSegments.get(0).getStart(), mSegments.get(0).getSpeed());
        }
		for (PathSegment segment : mSegments) {
			double distance = position_inverse.translateBy(segment.getEnd()).norm();
			if (distance >= lookahead_distance) {
				//This segment contains the lookahead point
				Optional<Translation2d> intersection_point = getFirstCircleSegmentIntersection(segment, position, lookahead_distance);
				if (intersection_point.isPresent()) {
					return new PathSegment.Sample(intersection_point.get(), segment.getSpeed());
				}
			}
		}
        //Special case: After the last point, so extrapolate forward.
        PathSegment last_segment = mSegments.get(mSegments.size() - 1);
        PathSegment new_last_segment = new PathSegment(last_segment.getStart(), last_segment.interpolate(10000), last_segment.getSpeed());
        Optional<Translation2d> intersection_point = getFirstCircleSegmentIntersection(new_last_segment, position, lookahead_distance);
		return intersection_point.map(translation2d -> new PathSegment.Sample(translation2d, last_segment.getSpeed())).orElseGet(() -> new PathSegment.Sample(last_segment.getEnd(), last_segment.getSpeed()));
    }

    static Optional<Translation2d> getFirstCircleSegmentIntersection(PathSegment segment, Translation2d center, double radius) {
        double x1 = segment.getStart().getX() - center.getX();
        double y1 = segment.getStart().getY() - center.getY();
        double x2 = segment.getEnd().getX() - center.getX();
        double y2 = segment.getEnd().getY() - center.getY();
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dr_squared = dx * dx + dy * dy;
        double det = x1 * y2 - x2 * y1;

        double discriminant = dr_squared * radius * radius - det * det;
        if (discriminant < 0) {
            //No intersection
            return Optional.empty();
        }

        double sqrt_discriminant = Math.sqrt(discriminant);
        Translation2d pos_solution = new Translation2d((det * dy + (dy < 0 ? -1 : 1) * dx * sqrt_discriminant) / dr_squared + center.getX(),
                (-det * dx + Math.abs(dy) * sqrt_discriminant) / dr_squared + center.getY());
        Translation2d neg_solution = new Translation2d((det * dy - (dy < 0 ? -1 : 1) * dx * sqrt_discriminant) / dr_squared + center.getX(),
                (-det * dx - Math.abs(dy) * sqrt_discriminant) / dr_squared + center.getY());

        //Choose the one between start and end that is closest to start
        double pos_dot_product = segment.dotProduct(pos_solution);
        double neg_dot_product = segment.dotProduct(neg_solution);
        if (pos_dot_product < 0 && neg_dot_product >= 0) {
            return Optional.of(neg_solution);
        } else if (pos_dot_product >= 0 && neg_dot_product < 0) {
            return Optional.of(pos_solution);
        } else {
            if (Math.abs(pos_dot_product) <= Math.abs(neg_dot_product)) {
                return Optional.of(pos_solution);
            } else {
                return Optional.of(neg_solution);
            }
        }
    }

    public List<Waypoint> getWayPoints() {
        return this.mWaypoints;
    }
}