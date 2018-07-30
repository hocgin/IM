package in.hocg.test.netty;

import in.hocg.test.netty.utils.Configuration;

/**
 * @author hocgin
 * @date 18-7-30
 **/
public class NettyApplication {
    
    public static void main(String[] args) {
        Configuration.init();
        int port = Integer.parseInt(Configuration.getProperty("netty.port", "9090"));
        NettyServer server = new NettyServer(port);
        server.start();
    }
    
}
