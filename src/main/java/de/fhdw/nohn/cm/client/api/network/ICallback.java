package de.fhdw.nohn.cm.client.api.network;

import io.netty.channel.Channel;

public interface ICallback {
	
	void onConnected(Channel channel);
}
