package de.fhdw.nohn.cm.client.api.network.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@SuppressWarnings("rawtypes")
public class MessageLengthSerializer extends MessageToByteEncoder {
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		if (!(msg instanceof ByteBuf)) return;
		
		ByteBuf in = (ByteBuf) msg;
		
		int readableBytes = in.readableBytes();
		int lengthByteSpace = PacketBuffer.getVarIntSize(readableBytes);
		
		if (lengthByteSpace > 3) {
			throw new IllegalArgumentException("unable to fit " + lengthByteSpace + " into 3");
		}
		PacketBuffer buffer = new PacketBuffer(out);
		buffer.ensureWritable(lengthByteSpace + readableBytes);
		buffer.writeVarInt(readableBytes);
		buffer.writeBytes(in, in.readerIndex(), readableBytes);
	}
}

