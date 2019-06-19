package cn.wc.rpc.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务持有工具
 *
 * @author WangCong
 * @date 2019-06-19
 * @since 1.0
 */
public final class ServiceHolder {
    private final static Map<String, Object> HOLDER = new ConcurrentHashMap<>(256);

    private ServiceHolder() {
        throw new AssertionError();
    }


    public static Object getService(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return HOLDER.get(id);
    }

    public static <T> T getService(String id, Class<T> clazz) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        Object service = HOLDER.get(id);
        if (Objects.nonNull(service) && clazz.isInstance(service)) {
            return clazz.cast(service);
        }
        return null;
    }

    public static void addService(String id, Object instance) {
        if (StringUtils.isNotBlank(id) && Objects.nonNull(instance)) {
            HOLDER.putIfAbsent(id, instance);
        }
    }
}
