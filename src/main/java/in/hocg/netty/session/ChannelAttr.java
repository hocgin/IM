package in.hocg.netty.session;

import io.netty.util.AttributeKey;

/**
 * @author hocgin
 * @date 18-7-31
 **/
public interface ChannelAttr {
    
    /**
     * 连接时间
     */
    AttributeKey<Long> LINKED_AT = AttributeKey.valueOf("LINKED_AT");
    
    /**
     * Username
     */
    AttributeKey<String> USERNAME = AttributeKey.valueOf("USERNAME");
}
