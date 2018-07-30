package in.hocg.test.netty.handler;

import in.hocg.test.netty.SessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.java.Log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        SessionManager.remove(ctx.channel().id());
    }
    
    /**
     * 用户连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
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
            Channel channel = ctx.channel();
            SessionManager.Session session = new SessionManager.Session(channel.id(), channel);
            SessionManager.put(session);
            log.info(String.format("[事件-握手完成] %s", evt.toString()));
            channel.writeAndFlush(new TextWebSocketFrame(String.format("UP:当前用户数量(%d)", SessionManager.list().size())));
        } else if (evt instanceof IdleStateEvent) {
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
            TextWebSocketFrame msg = new TextWebSocketFrame(String.format("UP:当前用户数量(%d)", SessionManager.list().size()));
            channel.writeAndFlush(msg);
            log.info(String.format("[事件-心跳] %s", evt.toString()));
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
        System.out.println(String.format("客户端消息: %s", msg.text()));
        SessionManager.list().stream()
                
                // 过滤掉自己
                .filter(channelId -> channelId != ctx.channel().id())
                
                // 广播
                .forEach(id -> {
                    SessionManager.get(id).ifPresent(session -> {
                        String idStr = session.getId().asLongText();
                        
                        // 缩短ID
                        idStr = String.format("%s...%s", idStr.subSequence(0, 3), idStr.subSequence(idStr.length() - 3, idStr.length()));
                        TextWebSocketFrame text = new TextWebSocketFrame(String.format("[%s]%s说:%s",
                                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                                idStr,
                                msg.text()));
                        session.getChannel().writeAndFlush(text);
                    });
                });
        
    }
}
