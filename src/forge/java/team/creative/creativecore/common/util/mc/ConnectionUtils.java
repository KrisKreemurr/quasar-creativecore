package team.creative.creativecore.common.util.mc;

import java.lang.reflect.Field;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.ProtocolInfo;

public class ConnectionUtils {
    
    /** Protocol of the packets this side receives. */
    public static ProtocolInfo<?> getProtocolInfo(Connection connection) {
        return connection.getInboundProtocol();
    }
    
    private static Field channelField;
    private static boolean warned;
    
    /** Protocol of the packets this side sends (Connection does not expose it; it lives on the netty encoder). Falls back to the inbound protocol. */
    public static ProtocolInfo<?> getOutboundProtocolInfo(Connection connection) {
        try {
            if (channelField == null)
                for (Field field : Connection.class.getDeclaredFields())
                    if (Channel.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        channelField = field;
                        break;
                    }
            Channel channel = (Channel) channelField.get(connection);
            if (channel != null)
                for (ChannelHandler handler : channel.pipeline().toMap().values())
                    if (handler instanceof net.minecraft.network.PacketEncoder<?> encoder && encoder.getProtocolInfo() != null)
                        return encoder.getProtocolInfo();
        } catch (ReflectiveOperationException | RuntimeException e) {
            if (!warned) {
                warned = true;
                org.apache.logging.log4j.LogManager.getLogger("creativecore").warn("Could not read the outbound protocol, falling back to the inbound one", e);
            }
        }
        return getProtocolInfo(connection);
    }
    
}
