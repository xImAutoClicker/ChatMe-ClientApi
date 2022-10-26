package de.fhdw.nohn.cm.client.api.network.packet.out;

import de.fhdw.nohn.cm.client.api.network.packet.PacketUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PacketOutLogin implements OutPacket {
	
	private final int packetId = 0;
	
	private String username;
	
	@Override
	public void writePacket(ByteBuf buffer) throws Exception {
		PacketUtil.writeString(buffer, this.username);
	}
}
