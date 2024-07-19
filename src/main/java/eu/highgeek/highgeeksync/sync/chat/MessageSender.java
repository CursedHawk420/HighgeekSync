package eu.highgeek.highgeeksync.sync.chat;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import eu.highgeek.highgeeksync.objects.Message;
import eu.highgeek.highgeeksync.utils.VersionHandler;

public class MessageSender {


    public static PacketContainer createChatPacket(Message message){

        final PacketContainer container;
        String toSend = message.getMessage();

        if (VersionHandler.isAtLeast_1_20_4()) { // 1.20.4+
            container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
            container.getChatComponents().write(0, WrappedChatComponent.fromText(toSend));
            container.getBooleans().write(0, false);
        } else if (VersionHandler.isAbove_1_19()) { // 1.19.1 -> 1.20.3
            container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
            container.getStrings().write(0, toSend);
            container.getBooleans().write(0, false);
        } else if (VersionHandler.isUnder_1_19()) { // 1.7 -> 1.19
            WrappedChatComponent component = WrappedChatComponent.fromText(toSend);
            container = new PacketContainer(PacketType.Play.Server.CHAT);
            container.getModifier().writeDefaults();
            container.getChatComponents().write(0, component);
        } else { // 1.19
            container = new PacketContainer(PacketType.Play.Server.SYSTEM_CHAT);
            container.getStrings().write(0, toSend);
            container.getIntegers().write(0, 1);
        }
        return container;
    }
}
