package cn.wc.rpc.consumer.utils;

import io.netty.channel.ChannelId;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放Rpc调用结果
 *
 * @author WangCong
 * @date 2019-06-19
 * @since 1.0
 */
public final class ResultKeeper {
    private final static Map<ChannelId, Object> KEEPER = new ConcurrentHashMap<>(256);

    private ResultKeeper() {
        throw new AssertionError();
    }

    public static boolean has(ChannelId id) {
        return KEEPER.containsKey(id);
    }

    public static Object get(ChannelId id) {
        if (Objects.nonNull(id)) {
            return KEEPER.remove(id);
        }
        return null;
    }

    public static void put(ChannelId id, Object value) {
        if (Objects.nonNull(id) && Objects.nonNull(value)) {
            KEEPER.putIfAbsent(id, value);
        }
    }
}
