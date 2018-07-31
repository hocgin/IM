package in.hocg.netty.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author hocgin
 * @date 18-7-31
 **/
public class Invoker {
    private Object target;
    private Method method;
    
    public Object getTarget() {
        return target;
    }
    
    public void setTarget(Object target) {
        this.target = target;
    }
    
    public Method getMethod() {
        return method;
    }
    
    public void setMethod(Method method) {
        this.method = method;
    }
    
    public static Invoker valueOf(Method method, Object target){
        Invoker invoker = new Invoker();
        invoker.setTarget(target);
        invoker.setMethod(method);
        return invoker;
    }
    
    
    public Object invoke(Object... paramValues){
        try {
            return method.invoke(target, paramValues);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
