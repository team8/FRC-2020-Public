package com.palyrobotics.frc2018.vision;

import com.palyrobotics.frc2018.config.Constants;

/**
 * Created by Alvin on 2/15/2017.
 */
public class AndroidComputerTest {

	public static void main(String[] args) throws InterruptedException {

		VisionManager.getInstance().start(Constants.kAndroidConnectionUpdateRate,true);

//		while (true) {
//
//			if (VisionManager.getInstance().isAppStarted()) {
//
////			System.out.println(String.format("X: %b, Y: %b", VisionData.getXData().exists(), VisionData.getZData().exists()));
////
//				final String x = Double.toString(VisionData.getXDataValue()), y = Double.toString(VisionData.getZDataValue());
//
//				System.out.println(String.format("X: %s, Y: %s", x, y));
//			}
//			Thread.sleep(500);
//		}
	}
}
