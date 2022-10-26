package de.fhdw.nohn.cm.client.api.network.netty;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

public class MessageLengthDeserializer extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf inBuffer, List<Object> out) throws Exception {
		inBuffer.markReaderIndex();
		
		byte[] lengthBytes = new byte[3];

		for (int i = 0; i < 3; i++) {
			if (!inBuffer.isReadable()) {
				inBuffer.resetReaderIndex();
				return;
			}
			lengthBytes[i] = inBuffer.readByte();
			
			// If the byte is higher/equal null. -> Length is available
			if (lengthBytes[i] >= 0) {
				PacketBuffer buffer = new PacketBuffer(Unpooled.wrappedBuffer(lengthBytes));
				
				try {
					int packetLength = buffer.readVarInt();
					
					if (inBuffer.readableBytes() < packetLength) {
						inBuffer.resetReaderIndex();
						return;
					}
					out.add(inBuffer.readBytes(packetLength));
				} finally {
					buffer.release();
				}
				return;
			}
		}
		throw new CorruptedFrameException("length wider than 21-bit");
	}

}

