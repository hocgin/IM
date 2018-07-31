package in.hocg.netty.core;

import com.google.common.reflect.ClassPath;
import io.netty.util.internal.PlatformDependent;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * @author hocgin
 * @date 18-7-31
 **/
public final class InvokerManager {
    private static final Map<String, Invoker> METHODS = PlatformDependent.newConcurrentHashMap();
    
    
    /**
     * 扫描业务入口
     *
     * @param scan
     * @throws Exception
     */
    public static void scan(Class scan) throws Exception {
        Package aPackage = scan.getPackage();
        ClassPath classPath = ClassPath.from(scan.getClassLoader());
        for (ClassPath.ClassInfo classInfo : classPath.getTopLevelClassesRecursive(aPackage.getName())) {
            Class<?> clazz = Class.forName(classInfo.getName());
            Module module = clazz.getAnnotation(Module.class);
            if (Objects.nonNull(module)) {
                Object bean = clazz.newInstance();
                for (Method method : clazz.getMethods()) {
                    Command command = method.getAnnotation(Command.class);
                    if (Objects.nonNull(command)) {
                        String key = genKey(module.value(), command.value());
                        if (METHODS.containsKey(key)) {
                            throw new IllegalAccessException("业务主键发生冲突");
                        } else {
                            METHODS.put(key, Invoker.valueOf(method, bean));
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 键码生成策略
     *
     * @param module
     * @param command
     * @return
     */
    private static String genKey(int module, int command) {
        return String.format("%s-%s", module, command);
    }
    
    
    /**
     * 获取执行器
     *
     * @param module
     * @param command
     * @return
     */
    public static Invoker getInvoker(int module, int command) {
        return METHODS.get(genKey(module, command));
    }
}
