package de.fhdw.nohn.cm.client.api.network.netty;

import java.util.List;

import de.fhdw.nohn.cm.client.api.network.packet.Packets;
import de.fhdw.nohn.cm.client.api.network.packet.in.InPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class MessageDeserializer extends ByteToMessageDecoder {
	
	/**
	 * Each ByteBuf starts with the PacketId. Then an instance 
	 * is created from the respective package.
	 */
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() > 0) {
			PacketBuffer buffer = new PacketBuffer(in);
			int packetId = buffer.readVarInt();
			
			InPacket packet = Packets.newPacketInstance(packetId);
			
			if (packet == null) {
				throw new Exception("InPacket not found! [PacketId: " + packetId + "]");
			}
			packet.readPacket(buffer);
			
			if (buffer.readableBytes() > 0) {
				throw new Exception("Error reading InPacket [PacketId: "+packetId+"]. "+buffer.readableBytes()+" bytes more than expected were found.");
			}
			out.add(packet);
		}
	}
}
