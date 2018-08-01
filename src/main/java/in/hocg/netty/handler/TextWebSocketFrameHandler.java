package in.hocg.netty.handler;

import in.hocg.netty.core.InvokerManager;
import in.hocg.netty.session.SessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.java.Log;

/**
 * @author hocgin
 * @date 18-7-30
 **/
@Log
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    
    /**
     * 用户下线
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        SessionManager.disconnect(ctx.channel());
    }
    
    /**
     * 用户连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        SessionManager.connect(channel);
        channel.writeAndFlush(new TextWebSocketFrame(String.format("UP:当前用户数量(%d)", SessionManager.pool().size())));
    }
    
    /**
     * 处理异常
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
    
    /**
     * 事件接收
     * - 所有事件都会经过该函数
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.ServerHandshakeStateEvent) {
            log.info(String.format("[事件-握手] %s", evt.toString()));
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            log.info(String.format("[事件-握手完成] %s", evt.toString()));
        } else if (evt instanceof IdleStateEvent) {
            log.info(String.format("[事件-心跳] %s", evt.toString()));
            Channel channel = ctx.channel();
            switch (((IdleStateEvent) evt).state()) {
                // 长时间未读取到内容
                case READER_IDLE:
                    break;
                // 长时间未发送内容
                case WRITER_IDLE:
                    break;
                // 长时间未读取和发送内容
                case ALL_IDLE:
                    channel.close();
                    return;
                default:
            }
            TextWebSocketFrame msg = new TextWebSocketFrame(String.format("UP:当前用户数量(%d)", SessionManager.pool().size()));
            channel.writeAndFlush(msg);
        } else {
            log.info(String.format("[事件-未定义] %s", evt.toString()));
        }
        super.userEventTriggered(ctx, evt);
    }
    
    /**
     * 接收内容并响应
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
    
        String[] split = msg.text().split(",");
        InvokerManager.getInvoker(Integer.valueOf(split[0]), Integer.valueOf(split[1])).invoke(ctx, msg);
    }
}
