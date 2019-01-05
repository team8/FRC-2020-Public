package com.team254.lib.trajectory;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AdaptivePurePursuitControllerTest.class, KinematicsTest.class, PathTest.class, TestRigidTransform2d.class })
public class TrajectoryTestSuite {

}
