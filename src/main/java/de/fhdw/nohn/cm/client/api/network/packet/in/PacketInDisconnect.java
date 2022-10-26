package de.fhdw.nohn.cm.client.api.network.packet.in;

import de.fhdw.nohn.cm.client.api.network.packet.PacketUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

@Getter
public class PacketInDisconnect implements InPacket {
	
	private final int packetId = 2;
	
	private String reason = "noReason";
	
	@Override
	public void readPacket(ByteBuf inBuffer) throws Exception {
		this.reason = PacketUtil.readString(inBuffer);
	}
}
