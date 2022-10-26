package de.fhdw.nohn.cm.client.api.network.event;

public abstract class Event<T> {

	public abstract void handleIncomingPacket(T packet);
}
