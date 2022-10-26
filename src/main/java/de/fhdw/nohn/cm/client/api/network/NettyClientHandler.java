package de.fhdw.nohn.cm.client.api.network;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import de.fhdw.nohn.cm.client.api.network.event.Event;
import de.fhdw.nohn.cm.client.api.network.packet.in.InPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NettyClientHandler extends SimpleChannelInboundHandler<InPacket> {
	
	private final NettyClient instance;
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("[ClientApi] channelActive called");
		
		instance.setChannel(ctx.channel());
		
		if(instance.getCallback() != null)
			instance.getCallback().onConnected(ctx.channel());
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, InPacket inPacket)
			throws Exception {
		System.out.println("[ClientApi] channelRead0 called");
		
		for(Event<?> event : instance.getEvents()) {
			Type genericSuperClass = event.getClass().getGenericSuperclass();
			ParameterizedType parametrizedType = null;
			
			while (parametrizedType == null) {
		        if ((genericSuperClass instanceof ParameterizedType)) {
		            parametrizedType = (ParameterizedType) genericSuperClass;
		        } else {
		            genericSuperClass = ((Class<?>) genericSuperClass).getGenericSuperclass();
		        }
		    }
			if(parametrizedType.getActualTypeArguments()[0] == inPacket.getClass()) {
				event.handleIncomingPacket(NettyClient.convertInstanceOfObject(inPacket));
			}
		}
		ReferenceCountUtil.release(inPacket);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("[ClientApi] exceptionCaught called");
		cause.printStackTrace();
	}
	
}
