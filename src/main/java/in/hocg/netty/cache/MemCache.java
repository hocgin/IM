package in.hocg.netty.cache;

import io.netty.channel.ChannelId;
import io.netty.util.internal.PlatformDependent;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hocgin
 * @date 18-8-1
 **/
public class MemCache implements Cache {
    
    // 所有连接
    public final static String ALL_CONNECT = "ALL_CONNECT";
    // 已经登录
    public final static String LOGIN_CONNECT = "LOGIN_CONNECT";
    
    private final static MemCache MEM_CACHE = new MemCache();
    
    public static MemCache getInstance() {
        return MEM_CACHE;
    }
    
    private final static Map<String, Map<String, ChannelId>> CHANNEL_POOLS = PlatformDependent.newConcurrentHashMap();
    
    @Override
    public void put(String poolName, String id, ChannelId channelId) {
        Map<String, ChannelId> map = CHANNEL_POOLS.get(poolName);
        if (Objects.isNull(map)) {
            map = PlatformDependent.newConcurrentHashMap();
            CHANNEL_POOLS.put(poolName, map);
        }
        map.put(id, channelId);
    }
    
    @Override
    public Optional<ChannelId> get(String poolName, String id) {
        Map<String, ChannelId> map = CHANNEL_POOLS.get(poolName);
        if (Objects.isNull(map) || !map.containsKey(id)) {
            return Optional.empty();
        }
        return Optional.of(map.get(id));
    }
    
    @Override
    public void remove(String poolName, String id) {
        Map<String, ChannelId> map = CHANNEL_POOLS.get(poolName);
        if (Objects.isNull(map)) {
            return;
        }
        map.remove(id);
    }
    
    public void remove(String id) {
    
    }
}
