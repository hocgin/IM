package in.hocg.netty.cache;

import io.netty.channel.ChannelId;

import java.util.Optional;

/**
 * @author hocgin
 * @date 18-8-1
 **/
public interface Cache {
    
    /**
     * 存储 Channel ID
     * @param poolName
     * @param id
     * @param channelId
     */
    void put(String poolName, String id, ChannelId channelId);
    
    /**
     * 取出 Channel ID
     * @param poolName
     * @param id
     * @return
     */
    Optional<ChannelId> get(String poolName, String id);
    
    /**
     * 移除 Channel ID
     * @param poolName
     * @param id
     */
    void remove(String poolName, String id);
}
