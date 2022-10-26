package de.fhdw.nohn.cm.client.api.network.packet.out;

import de.fhdw.nohn.cm.client.api.network.packet.PacketUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PacketOutMessage implements OutPacket {

	private final int packetId = 1;
	
	private String serverName, username, message;
	
	private long millis;
	
	@Override
	public void writePacket(ByteBuf buffer) throws Exception {
		PacketUtil.writeString(buffer, this.serverName);
		PacketUtil.writeString(buffer, this.username);
		PacketUtil.writeString(buffer, this.message);
		buffer.writeLong(this.millis);
	}
}
