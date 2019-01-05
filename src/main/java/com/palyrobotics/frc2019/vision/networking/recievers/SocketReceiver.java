package com.palyrobotics.frc2018.vision.networking.recievers;

import com.palyrobotics.frc2018.vision.util.AbstractVisionServer;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.logging.Level;

public abstract class SocketReceiver extends AbstractVisionServer {

	public SocketReceiver(final String name) {
		super(name);
	}

//	public String extractData() {
//		if (m_Client.isConnected() && !m_Client.isClosed()) {
//			final StringBuilder data = new StringBuilder();
//			try {
//				String line;
//				final BufferedReader reader = new BufferedReader(new InputStreamReader(m_Client.getInputStream()));
//				while ((line = reader.readLine()) != null) {
//					data.append(line);
//					data.append("\n");
//				}
//				return data.toString();
//			} catch(final IOException e) {
//				log(Level.FINEST, e.toString());
//				return null;
//			}
//		}
//		return null;
//	}

	@Override protected void afterInit() { }

	/**
	 * Extract byte array data from the client.
	 *
	 * @return Byte array from socket
	 */
	protected byte[] extractDataBytes() {
		try {
            try {
				final int length = m_ClientInputStream.readInt();
				final byte[] data = new byte[length];
                m_ClientInputStream.readFully(data, 0, length);
				return data;
			} catch (final EOFException eofe) {
				log(Level.FINEST, eofe.toString());
				closeServer();
			}
		} catch (final IOException ioe) {
			log(Level.FINEST, ioe.toString());
			closeServer();
		}
		return null;
	}

	protected abstract void processData(final byte[] data);

	@Override
	protected void afterUpdate() {
		switch (m_ThreadState) {
			case RUNNING: {
				switch (m_ServerState) {
					case OPEN: {
						processData(extractDataBytes());
						break;
					}
				}
				break;
			}
		}
	}
}
