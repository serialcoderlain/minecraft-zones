package com.mycompanioncube.zones;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

/**
 * Sends the zone name change to players when they transition in or out of a
 * zone.
 * 
 * @author Serial Coder Lain (serialcoderlain@gmail.com)
 */
public class AreaChangeMessage implements IMessage, IMessageHandler<AreaChangeMessage, IMessage> {

	private String text;

	public AreaChangeMessage() {
	}

	public AreaChangeMessage(String text) {
		this.text = text;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		text = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, text);
	}

	@Override
	public IMessage onMessage(AreaChangeMessage message, MessageContext ctx) {
		Zones.instance.setMessage(message.text);
		return null;
	}
}