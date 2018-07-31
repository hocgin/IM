package in.hocg;

import in.hocg.netty.NettyServer;
import in.hocg.netty.core.InvokerManager;
import in.hocg.netty.utils.Configuration;

/**
 * @author hocgin
 * @date 18-7-30
 **/
public class NettyApplication {
    
    
    public static void main(String[] args) throws Exception {
        /**
         * 扫描业务
         */
        InvokerManager.scan(NettyApplication.class);
        Configuration.init();
        int port = Integer.parseInt(Configuration.getProperty("netty.port", "9090"));
        NettyServer server = new NettyServer(port);
        server.start();
    }
    
    
}
