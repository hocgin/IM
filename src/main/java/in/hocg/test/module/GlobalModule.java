package in.hocg.test.module;

import in.hocg.netty.core.Command;
import in.hocg.netty.core.Module;
import in.hocg.netty.session.SessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * @author hocgin
 * @date 18-8-1
 **/
@Module(0)
public class GlobalModule {
    
    /**
     * 连接后
     */
    @Command(0)
    public void connected(ChannelHandlerContext ctx) {
    }
    
}
