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
		/*
		* NOTE: To get an Array of the specific class type which the instance is using,
		* you have to use this specific code:
		* specificCircularBufferGeneric.getLinkedList().toArray(new ClassThatIWant[specificCircularBufferGeneric
		* .getLinkedList().size()]);
		* The reason is that for some reason an array of a generic class(i.e. E[]) cannot be created because
		* of some archaic data flow ambiguities
		*/
		return mSamples;
	}
}
