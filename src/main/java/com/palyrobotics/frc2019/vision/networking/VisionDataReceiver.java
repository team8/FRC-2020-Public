package com.palyrobotics.frc2018.vision.networking;

import com.palyrobotics.frc2018.vision.VisionData;
import com.palyrobotics.frc2018.vision.networking.recievers.SocketReceiver;

import java.util.concurrent.ConcurrentLinkedQueue;

public class VisionDataReceiver extends SocketReceiver {

	private static final int MAX_FRAME_QUEUE_SIZE = 2;

	public VisionDataReceiver() {
		super("Video Receiver");
		// super("Video Receiver", Constants.kVisionVideoFileName, Constants.kVisionVideoReceiverSocketPort, Constants.kVisionVideoReceiverUpdateRate, false);
	}

	@Override
	protected void processData(final byte[] image) {
		final ConcurrentLinkedQueue<byte[]> frameQueue = VisionData.getVideoQueue();
		if (image != null && image.length != 0) {
			// Make sure queue does not get too big
			while (frameQueue.size() > MAX_FRAME_QUEUE_SIZE)
				frameQueue.remove();
			frameQueue.add(image);
		}
	}
}
