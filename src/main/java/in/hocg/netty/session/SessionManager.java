package in.hocg.netty.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author hocgin
 * @date 18-7-30
 **/
public final class SessionManager {
    
    private SessionManager() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * 所有连接
     */
    private static final DefaultChannelGroup ALL_CONNECT = new DefaultChannelGroup("ALL_CONNECT", GlobalEventExecutor.INSTANCE);
    
    public static DefaultChannelGroup pool() {
        return ALL_CONNECT;
    }
    
    public static void connect(Channel channel) {
        channel.attr(AttributeKey.valueOf(ChannelAttr.LINKED_AT)).set(System.currentTimeMillis());
        ALL_CONNECT.add(channel);
    }
    
    public static void disconnect(ChannelId id) {
        ALL_CONNECT.remove(id);
    }
    
    public static void disconnect(Channel channel) {
        ALL_CONNECT.remove(channel);
    }
}
