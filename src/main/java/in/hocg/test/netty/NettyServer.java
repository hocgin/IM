package in.hocg.test.netty;

import in.hocg.test.netty.handler.TextWebSocketFrameHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author hocgin
 * @date 18-7-30
 **/
public final class NettyServer extends ChannelInitializer<Channel> implements Server {
    private int port;
    ServerBootstrap bootstrap;
    
    public NettyServer(int port) {
        this.port = port;
    }
    
    @Override
    public Server start() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            bootstrap = new ServerBootstrap();
            bootstrap.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(this)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .bind(port)
                    .channel()
                    .closeFuture()
                    .sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
        return this;
    }
    
    @Override
    public Server shutdown() {
        bootstrap.config().group().shutdownGracefully();
        bootstrap.config().childGroup().shutdownGracefully();
        return this;
    }
    
    
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                
                // 打印帧信息
                .addLast("logging", new LoggingHandler(LogLevel.INFO))
                
                /**
                 * 心跳检测
                 * - 500秒内未读取到客户端数据,会触发读超时
                 * - 1秒内未向客户端发送数据,会触发写超时
                 */
                .addLast(new IdleStateHandler(5, 1, 5, TimeUnit.SECONDS))
                
                // HTTP协议,编解码
                .addLast("http-codec", new HttpServerCodec())
                
                /**
                 * HTTP 消息合并
                 * - 当消息超过 65536 会发生异常,可以对消息进行分包或增大参数容量
                 */
                .addLast("aggregator", new HttpObjectAggregator(65536))
                
                // 分块处理
                .addLast("http-chunked", new ChunkedWriteHandler())
                
                // Socket 协议处理
                .addLast("webSocketServerProtocolHandler", new WebSocketServerProtocolHandler("/ws"))
                
                // 业务处理
                .addLast(new TextWebSocketFrameHandler())
        ;
    }
}
