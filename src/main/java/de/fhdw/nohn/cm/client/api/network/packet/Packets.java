package de.fhdw.nohn.cm.client.api.network.packet;

import java.util.HashMap;
import java.util.Map;

import de.fhdw.nohn.cm.client.api.network.packet.in.InPacket;
import de.fhdw.nohn.cm.client.api.network.packet.in.PacketInDisconnect;
import de.fhdw.nohn.cm.client.api.network.packet.in.PacketInMessage;
import de.fhdw.nohn.cm.client.api.network.packet.in.PacketInServerInfo;

public class Packets {
	
	private static final Map<Integer, Class<? extends InPacket>> packets = new HashMap<>();
	
	static {
		registerPacket(2, PacketInDisconnect.class);
		registerPacket(1, PacketInServerInfo.class);
		registerPacket(0, PacketInMessage.class);
	}
	
	public static InPacket newPacketInstance(final int packetId) throws Exception {
		Class<? extends InPacket> packetClass = Packets.getPacketClass(packetId);
		
		if(packetClass == null)
			return null;
		
		return packetClass.getDeclaredConstructor().newInstance();
	}
	
	public static void registerPacket(final int packetId, final Class<? extends InPacket> packetClass) {
		packets.put(packetId, packetClass);
	}
	
	public static Class<? extends InPacket> getPacketClass(final int packetId) {
		return packets.get(packetId);
	}
}
