package de.fhdw.nohn.cm.client.api.network.packet.in;

import de.fhdw.nohn.cm.client.api.network.packet.PacketUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PacketInMessage implements InPacket {

	private final int packetId = 0;
	
	private String username, message;
	
	private long dateInMillis;
	
	@Override
	public void readPacket(ByteBuf inBuffer) throws Exception {
		this.username = PacketUtil.readString(inBuffer);
		this.message = PacketUtil.readString(inBuffer);
		this.dateInMillis = inBuffer.readLong();
	}
}
