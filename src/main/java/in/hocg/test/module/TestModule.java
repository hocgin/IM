package in.hocg.test.module;

import in.hocg.netty.core.Command;
import in.hocg.netty.core.Module;
import in.hocg.netty.session.ChannelAttr;
import in.hocg.netty.session.SessionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hocgin
 * @date 18-7-31
 **/
@Module(1)
public class TestModule {
    public static AtomicInteger ID = new AtomicInteger(0);
    
    @Command(0)
    public void index(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        Channel sender = ctx.channel();
        
        autoSetUsername(sender);
        
        String message = msg.text().split(",")[2];
        System.out.println(String.format("客户端消息: %s", message));
    
        { // 发送给自己
            TextWebSocketFrame text = new TextWebSocketFrame(String.format("你(%s)说:%s",
                    sender.attr(ChannelAttr.USERNAME).get(),
                    message));
            sender.writeAndFlush(text);
        }
        
        SessionManager.pool().parallelStream()
                
                // 判断是否自己
                .filter(c -> c != ctx.channel())
                
                // 广播
                .forEach(receive -> {
                    String idStr = id(sender.id());
                    TextWebSocketFrame text = new TextWebSocketFrame(String.format("[%s]%s说:%s",
                            LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                            idStr,
                            message));
                    receive.writeAndFlush(text);
                });
    }
    
    private void autoSetUsername(Channel sender) {
        // 临时分配ID
        if (Objects.isNull(sender.attr(ChannelAttr.USERNAME).get())) {
            sender.attr(ChannelAttr.USERNAME).set(String.valueOf(ID.getAndAdd(1)));
        }
    }
    
    // 缩短ID
    private String id(ChannelId id) {
        String idStr = id.asShortText();
//        idStr = String.format("%s...%s", idStr.subSequence(0, 3), idStr.subSequence(idStr.length() - 3, idStr.length()));
        return idStr;
    }
    
    
    @Command(1)
    public void toUser(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        String[] split = msg.text().split(",")[2].split(":");
        if (Objects.nonNull(split[0]) && split[0].length() > 0) {
            Channel sender = ctx.channel();
            String message = split[1];
            { // 发给自己
                TextWebSocketFrame text = new TextWebSocketFrame(String.format("你(%s)说:%s",
                        sender.attr(ChannelAttr.USERNAME).get(),
                        message));
                sender.writeAndFlush(text);
            }
            SessionManager.pool().forEach(receive -> {
                if (split[0].equals(receive.attr(ChannelAttr.USERNAME).get())) {
                    TextWebSocketFrame text = new TextWebSocketFrame(String.format("%s(%s) 悄悄对你(%s)说:%s", id(sender.id()), sender.attr(ChannelAttr.USERNAME).get(), id(receive.id()), message));
                    receive.writeAndFlush(text);
                }
            });
        }
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
