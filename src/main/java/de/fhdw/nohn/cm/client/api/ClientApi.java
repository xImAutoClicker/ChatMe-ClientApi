package de.fhdw.nohn.cm.client.api;

import de.fhdw.nohn.cm.client.api.network.ICallback;
import de.fhdw.nohn.cm.client.api.network.NettyClient;
import de.fhdw.nohn.cm.client.api.network.event.handler.MessageHandler;
import de.fhdw.nohn.cm.client.api.network.event.handler.ServerHandler;
import de.fhdw.nohn.cm.client.api.network.packet.in.PacketInServerInfo;
import de.fhdw.nohn.cm.client.api.network.packet.out.PacketOutLogin;
import de.fhdw.nohn.cm.client.api.network.packet.out.PacketOutMessage;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class ClientApi {

	private final String userName;
	
	private NettyClient loginClient, chatClient;
	
	@Setter
	private PacketInServerInfo serverInfo;
	
	public final void connectToLoginServer(final String host, final ServerHandler serverHandler) {
		this.loginClient = new NettyClient(host, 1999);
		this.loginClient.registerEvent(serverHandler);
		
		this.loginClient.setCallback(new ICallback() {
			
			@Override
			public void onConnected(Channel channel) {
				channel.writeAndFlush(new PacketOutLogin(userName));
			}
		});
		this.loginClient.connect();
	}
	
	public final void connectToChatServer(final String host, final int port, ICallback callback, final MessageHandler messageHandler) {
		this.chatClient = new NettyClient(host, port);
		this.chatClient.registerEvent(messageHandler);
		this.chatClient.setCallback(callback);
		this.chatClient.connect();
	}
	
//	@Override
//	public void handleIncomingPacket(PacketInServerInfo packet) {
//		this.serverInfo = packet;
//		this.connectToChatServer(packet.getIp(), packet.getPort());
//	}
	
	public final void sendMessage(final String message) {
		this.chatClient.sendPacket(new PacketOutMessage(this.serverInfo.getServerName(), this.userName, message, System.currentTimeMillis()));
	}
}
