package com.palyrobotics.frc2020.util;

public class Color {

	private Color() {
	}

	public static class HSV {

		private int h, s, v;
		private int lastH, lastS, lastV;
		public static final Color.HSV kBlue = new HSV(100, 150, 150),
				kWhite = new HSV(0, 75, 20),
				kLime = new HSV(60, 255, 20),
				kRed = new HSV(2, 247, 87),
				kPurple = new HSV(180, 247, 20),
				kAqua = new HSV(85, 247, 20),
				kNothing = new HSV(0, 0, 0);

		public HSV() {
		}

		public HSV(int h, int s, int v) {
			this.h = h;
			this.s = s;
			this.v = v;
		}

		public int getH() {
			return h;
		}

		public int getS() {
			return s;
		}

		public int getV() {
			return v;
		}

		public int[] getHSV() {
			return new int[] { h, s, v };
		}

		public void setH(int h) {
			lastH = this.h;
			this.h = h;
		}

		public void setS(int s) {
			lastS = this.s;
			this.s = s;
		}

		public void setV(int v) {
			lastV = this.v;
			this.v = v;
		}

		public void setHSV(int h, int s, int v) {
			lastH = this.h;
			lastS = this.s;
			lastV = this.v;
			this.h = h;
			this.s = s;
			this.v = v;
		}

		public Color.HSV getLastColor() {
			return new HSV(lastH, lastS, lastV);
		}
	}

	public static class RGB {

		private int r, g, b;

		public RGB(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public int getR() {
			return r;
		}

		public int getG() {
			return g;
		}

		public int getB() {
			return b;
		}

		public int[] getRGB() {
			return new int[] { r, g, b };
		}

		public void setR(int r) {
			this.r = r;
		}

		public void setG(int g) {
			this.g = g;

		}

		public void setB(int b) {
			this.b = b;
		}

		public void setRGB(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}
}
