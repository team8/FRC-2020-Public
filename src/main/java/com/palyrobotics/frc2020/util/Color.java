package com.palyrobotics.frc2020.util;

public class Color {

	private Color() {
	}

	public static class HSV {

		private int mH, mS, mV;
		private int mLastH, mLastS, mLastV;
		public static final Color.HSV kBlue = new HSV(100, 255, 87),
				kWhite = new HSV(0, 75, 20),
				kLime = new HSV(60, 255, 20),
				kYellow = new HSV(30, 255, 20),
				kGreen = new HSV(70, 255, 20),
				kOrange = new HSV(2, 247, 87),
				kPurple = new HSV(180, 247, 20),
				kAqua = new HSV(85, 247, 20),
				kRed = new HSV(0, 255, 100),
				kPink = new HSV(0, 200, 100),
				kOff = new HSV(0, 0, 0);

		public HSV() {
		}

		public HSV(int h, int s, int v) {
			mH = h;
			mS = s;
			mV = v;
		}

		public int getH() {
			return mH;
		}

		public int getS() {
			return mS;
		}

		public int getV() {
			return mV;
		}

		public int[] getHSV() {
			return new int[] { mH, mS, mV };
		}

		public void setH(int h) {
			mLastH = mH;
			mH = h;
		}

		public void setS(int s) {
			mLastS = mS;
			mS = s;
		}

		public void setV(int v) {
			mLastV = mV;
			mV = v;
		}

		public void setHSV(int h, int s, int v) {
			mLastH = mH;
			mLastS = mS;
			mLastV = mV;
			mH = h;
			mS = s;
			mV = v;
		}

		public Color.HSV getLastColor() {
			return new HSV(mLastH, mLastS, mLastV);
		}
	}

	public static class RGB {

		private int mR, mG, mB;

		public RGB(int r, int g, int b) {
			mR = r;
			mG = g;
			mB = b;
		}

		public int getR() {
			return mR;
		}

		public int getG() {
			return mG;
		}

		public int getB() {
			return mB;
		}

		public int[] getRGB() {
			return new int[] { mR, mG, mB };
		}

		public void setR(int r) {
			mR = r;
		}

		public void setG(int g) {
			mG = g;

		}

		public void setB(int b) {
			mB = b;
		}

		public void setRGB(int r, int g, int b) {
			mR = r;
			mG = g;
			mB = b;
		}
	}
}
