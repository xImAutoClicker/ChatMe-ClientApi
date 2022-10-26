package de.fhdw.nohn.cm.client.api.network.event.handler;

import de.fhdw.nohn.cm.client.api.network.event.Event;
import de.fhdw.nohn.cm.client.api.network.packet.in.PacketInDisconnect;

public class DisconnectHandler extends Event<PacketInDisconnect> {

	@Override
	public void handleIncomingPacket(PacketInDisconnect packet) {
	}
}
