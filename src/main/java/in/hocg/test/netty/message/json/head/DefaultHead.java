package in.hocg.test.netty.message.json.head;

import in.hocg.test.netty.message.Head;
import lombok.Data;

/**
 * @author hocgin
 * @date 18-7-30
 **/
@Data
public class DefaultHead implements Head {
    /**
     * 来源: 网站
     */
    private int src;
    /**
     * 消息类型: 发送给用户
     */
    private int type;
}
