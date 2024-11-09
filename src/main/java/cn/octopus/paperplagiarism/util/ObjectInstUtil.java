package cn.octopus.paperplagiarism.util;

import java.util.concurrent.ConcurrentHashMap;

public class ObjectInstUtil {
    // 使用 ConcurrentHashMap 存储单例，键为 Class<?>，值为 Object
    private static final ConcurrentHashMap<Class<?>, Object> instances = new ConcurrentHashMap<>();

    // 私有构造方法，防止实例化
    private ObjectInstUtil() {}

    // 泛型工厂方法，用于创建或获取单例
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> clazz, Supplier<T> supplier) {
        // 尝试从 map 中获取实例，如果不存在则使用 supplier 创建并放入 map
        return (T) instances.computeIfAbsent(clazz, k -> supplier.get());
    }

    // 泛型接口，用于创建实例
    @FunctionalInterface
    public interface Supplier<T> {
        T get();
    }
}
