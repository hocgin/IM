package in.hocg;

import in.hocg.netty.core.Invoker;
import in.hocg.netty.core.InvokerManager;

/**
 * @author hocgin
 * @date 18-7-31
 **/
public class Test {
    public static void main(String[] args) throws Exception {
        InvokerManager.scan(Test.class);
        for (int i = 2; i <= 4; i++) {
            Invoker invoker = InvokerManager.getInvoker(1, i);
            System.out.println(invoker.invoke());
        }
    }
}
