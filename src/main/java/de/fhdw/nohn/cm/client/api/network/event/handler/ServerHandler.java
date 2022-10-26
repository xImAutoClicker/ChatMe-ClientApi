package de.fhdw.nohn.cm.client.api.network.event.handler;

import de.fhdw.nohn.cm.client.api.network.event.Event;
import de.fhdw.nohn.cm.client.api.network.packet.in.PacketInServerInfo;

public class ServerHandler extends Event<PacketInServerInfo> {

	@Override
	public void handleIncomingPacket(PacketInServerInfo packet) { }
}
