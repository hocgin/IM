package in.hocg.test.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hocgin
 * @date 18-7-30
 **/
public class SessionManager {
    private static Map<ChannelId, Session> SESSIONS = new ConcurrentHashMap<>();
    private static SessionManager ME = new SessionManager();
    
    /**
     * 存储登录用户
     *
     * @param session
     * @return
     */
    public static SessionManager put(Session session) {
        
        // 拒绝未知用户
        if (Objects.isNull(session.getId())) {
            return ME;
        }
        
        // 登录用户
        SESSIONS.put(session.getId(), session);
        return ME;
    }
    
    /**
     * 获取登录用户
     *
     * @param id
     * @return
     */
    public static Optional<Session> get(ChannelId id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }
    
    /**
     * 获取所有会话 ID
     *
     * @return
     */
    public static Collection<ChannelId> list() {
        return SESSIONS.keySet();
    }
    
    /**
     * 移除会话
     * @param id
     * @return
     */
    public static SessionManager remove(ChannelId id) {
        get(id).ifPresent(session -> {
            Channel channel = session.getChannel();
            if (channel.isActive() && channel.isOpen()) {
                channel.close();
            }
            SESSIONS.remove(id);
        });
        return ME;
    }
    
    
    @Data
    @AllArgsConstructor
    public static class Session {
        private ChannelId id;
        private Channel channel;
    }
}
