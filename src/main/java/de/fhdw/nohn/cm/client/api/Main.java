package de.fhdw.nohn.cm.client.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import de.fhdw.nohn.cm.client.api.network.ICallback;
import de.fhdw.nohn.cm.client.api.network.event.handler.MessageHandler;
import de.fhdw.nohn.cm.client.api.network.event.handler.ServerHandler;
import de.fhdw.nohn.cm.client.api.network.packet.in.PacketInMessage;
import de.fhdw.nohn.cm.client.api.network.packet.in.PacketInServerInfo;
import io.netty.channel.Channel;

public class Main {

	public static void main(String[] args) {
		System.out.println("   _____ _           _   __  __ ______    _____ _ _            _   \r\n"
				+ "  / ____| |         | | |  \\/  |  ____|  / ____| (_)          | |  \r\n"
				+ " | |    | |__   __ _| |_| \\  / | |__    | |    | |_  ___ _ __ | |_ \r\n"
				+ " | |    | '_ \\ / _` | __| |\\/| |  __|   | |    | | |/ _ \\ '_ \\| __|\r\n"
				+ " | |____| | | | (_| | |_| |  | | |____  | |____| | |  __/ | | | |_ \r\n"
				+ "  \\_____|_| |_|\\__,_|\\__|_|  |_|______|  \\_____|_|_|\\___|_| |_|\\__|\r\n"
				+ "                                                                   \r\n"
				+ "                                                                   ");
		
		System.out.println("Your username: " + args[0]);
		
		ClientApi clientApi = new ClientApi(args[0]);
		clientApi.connectToLoginServer("localhost", new ServerHandler() {
			
			@Override
			public void handleIncomingPacket(PacketInServerInfo packet) {
				clientApi.setServerInfo(packet);
				
				System.out.println("[ServerHandler]: " + packet.getIp() + ":"+packet.getPort() + " " + packet.getServerName());
				
				clientApi.connectToChatServer(packet.getIp(), packet.getPort(), new ICallback() {
					
					@Override
					public void onConnected(Channel channel) {
						BufferedReader reader = new BufferedReader(
					            new InputStreamReader(System.in));
						
						while(true) {
							try {
								String message = reader.readLine();
								
								clientApi.sendMessage(message);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}, new MessageHandler() {
					
					@Override
					public void handleIncomingPacket(PacketInMessage packet) {
						System.out.println("[Message] " + packet.getUsername() + ": " + packet.getMessage());
					}
				});
			}
		});
	}
	
	public static String getHostIp() {
		return "127.0.0.1";
	}
}
