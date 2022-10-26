package de.fhdw.nohn.cm.client.api.network.packet.in;

import de.fhdw.nohn.cm.client.api.network.packet.PacketUtil;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PacketInServerInfo implements InPacket {
	
	private final int packetId = 1;
	
	private String serverName, ip;
	
	private int port;
	
	@Override
	public void readPacket(ByteBuf inBuffer) throws Exception {
		this.serverName = PacketUtil.readString(inBuffer);
		this.ip = PacketUtil.readString(inBuffer);
		this.port = inBuffer.readInt();
	}
}
