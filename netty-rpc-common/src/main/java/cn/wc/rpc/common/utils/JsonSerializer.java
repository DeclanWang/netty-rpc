package cn.wc.rpc.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.IOUtils;
import com.sun.istack.internal.NotNull;

/**
 * JSON序列化工具
 *
 * @author WangCong
 * @date 2019-06-18
 * @since 1.0
 */
public class JsonSerializer {

    private final static ParserConfig PARSER_CONFIG = new ParserConfig();

    static {
        PARSER_CONFIG.setAutoTypeSupport(true);
    }

    private JsonSerializer() {
        throw new AssertionError();
    }

    public static byte[] serialize(@NotNull Object obj) {
        return JSON.toJSONBytes(obj, SerializerFeature.WriteClassName);
    }

    public static Object deserialize(@NotNull byte[] bytes) {
        return deserialize(bytes, Object.class);
    }

    public static <T> T deserialize(@NotNull byte[] bytes, @NotNull Class<T> clazz) {
        return JSON.parseObject(new String(bytes, IOUtils.UTF8), clazz, PARSER_CONFIG);
    }
}
