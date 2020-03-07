package com.palyrobotics.frc2020.util;

import java.util.LinkedList;
import java.util.function.Predicate;

public class CircularBuffer<E> {

	private final int mWindowSize;
	private final LinkedList<E> mSamples = new LinkedList<>();

	public CircularBuffer(int windowSize) {
		mWindowSize = windowSize;
	}

	public void clear() {
		mSamples.clear();
	}

	public void add(E val) {
		mSamples.addLast(val);
		while (mSamples.size() > mWindowSize) {
			mSamples.removeFirst();
		}
	}

	public long numberOfOccurrences(E value) {
		return mSamples.stream()
				.filter(value::equals)
				.count();
	}

	public long numberOfOccurrences(Predicate<E> predicate) {
		return mSamples.stream()
				.filter(predicate)
				.count();
	}

	public int size() {
		return mSamples.size();
	}
}
