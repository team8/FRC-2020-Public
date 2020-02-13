package com.palyrobotics.frc2020.util;

import java.util.LinkedList;
import java.util.List;

public class CircularBuffer<E> {

	final int mWindowSize;
	final LinkedList<E> mSamples = new LinkedList<>();

	public CircularBuffer(int windowSize) {
		mWindowSize = windowSize;
	}

	public void clear() {
		mSamples.clear();
	}

	public void addValue(E val) {
		mSamples.addLast(val);
		if (mSamples.size() > mWindowSize) {
			mSamples.removeFirst();
		}
	}

	public List<E> samples() {
		return mSamples;
	}
}
