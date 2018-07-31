package in.hocg.test.module;

import in.hocg.netty.core.Command;
import in.hocg.netty.core.Module;
import in.hocg.netty.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author hocgin
 * @date 18-7-31
 **/
@Module(1)
public class TestModule {
    
    @Command(0)
    public void index(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        System.out.println(String.format("客户端消息: %s", msg.text()));
        SessionManager.pool().stream()
            
                // 过滤掉自己
                .filter(channel -> channel != ctx.channel())
            
                // 广播
                .forEach(channel -> {
                    String idStr = channel.id().asLongText();
                
                    // 缩短ID
                    idStr = String.format("%s...%s", idStr.subSequence(0, 3), idStr.subSequence(idStr.length() - 3, idStr.length()));
                    TextWebSocketFrame text = new TextWebSocketFrame(String.format("[%s]%s说:%s",
                            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                            idStr,
                            msg.text()));
                    channel.writeAndFlush(text);
                });
    }
    
    @Command(2)
    public void index2() {
        System.out.println("Test success2");
    }
    
    @Command(3)
    public void index3() {
        System.out.println("Test success3");
    }
    
    @Command(4)
    public void index4() {
        System.out.println("Test success4");
    }
}
