package de.fhdw.nohn.cm.client.api.network;

import java.util.ArrayList;
import java.util.List;

import de.fhdw.nohn.cm.client.api.network.event.Event;
import de.fhdw.nohn.cm.client.api.network.netty.MessageDeserializer;
import de.fhdw.nohn.cm.client.api.network.netty.MessageLengthDeserializer;
import de.fhdw.nohn.cm.client.api.network.netty.MessageLengthSerializer;
import de.fhdw.nohn.cm.client.api.network.netty.MessageSerializer;
import de.fhdw.nohn.cm.client.api.network.packet.in.InPacket;
import de.fhdw.nohn.cm.client.api.network.packet.out.OutPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class NettyClient {
	
	private final String host;
	
	private final int port;
	
	@Setter
	private Channel channel;
	
	private List<Event<?>> events = new ArrayList<>();
	
	@Setter
	private ICallback callback;
	
	
	public void connect() {
		final NettyClient instance = this;
		
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		
		try {
			Class<? extends Channel> clazz = NioSocketChannel.class;
			
			final Bootstrap bootstrap = new Bootstrap();
			
			bootstrap.group(eventLoopGroup).channel(clazz)
					.handler(new ChannelInitializer<SocketChannel>() {
						
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							socketChannel.pipeline().addLast("splitter", new MessageLengthDeserializer());
							socketChannel.pipeline().addLast("decoder", new MessageDeserializer());
							socketChannel.pipeline().addLast("prepender", new MessageLengthSerializer());
							socketChannel.pipeline().addLast("encoder", new MessageSerializer());
							socketChannel.pipeline().addLast("handler", new NettyClientHandler(instance));
						}
					});
			Channel channel;
			
			System.out.println("[ClientApi] Connecting to: " + getHost() + ":" + getPort());
			
			ChannelFuture channelFuture = bootstrap.connect(getHost(), getPort());
			channel = channelFuture.channel();
			
			ChannelFuture closeFuture = channel.closeFuture();			
			
			closeFuture.addListener(listener -> {
				System.out.println("[ClientApi] Can't connect to Login-Server! Reconnect in 2 seconds...");
				Thread.sleep(2000);
				instance.connect();
				eventLoopGroup.shutdownGracefully();
			});
			closeFuture.sync();
		} catch (Exception exc) {
			System.out.println("[ClientApi] Error called:");
			exc.printStackTrace();
		} finally {
			eventLoopGroup.shutdownGracefully();
		}
		
	}
	
	public final void sendPacket(final OutPacket packet) {
		this.channel.writeAndFlush(packet);
	}
	
	public final void registerEvent(final Event<? extends InPacket> event) {
		this.events.add(event);
	}
	
	public final void unregisterEvent(final Event<? extends InPacket> event) {
		this.events.remove(event);
	}
	
	// https://stackoverflow.com/questions/14524751/cast-object-to-generic-type-for-returning
	@SuppressWarnings("unchecked")
	public static <T> T convertInstanceOfObject(Object o) {
	    try {
	       return (T) o;
	    } catch (ClassCastException e) {
	        return null;
	    }
	}
}
