package de.fhdw.nohn.cm.client.api.network.packet.in;

import io.netty.buffer.ByteBuf;

public interface InPacket {
	
	int getPacketId();
	
	void readPacket(final ByteBuf inBuffer) throws Exception;
}
