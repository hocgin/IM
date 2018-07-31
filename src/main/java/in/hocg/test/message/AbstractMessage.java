package in.hocg.test.message;

import lombok.Data;

/**
 * @author hocgin
 * @param <H>
 * @param <B>
 */
@Data
public abstract class AbstractMessage<H extends Head, B extends Body> implements Bytes {
    
    /**
     * 消息头
     */
    private H head;
    
    /**
     * 消息体
     */
    private B body;
    
    
}
