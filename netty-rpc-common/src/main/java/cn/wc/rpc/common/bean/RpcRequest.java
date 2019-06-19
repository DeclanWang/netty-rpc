package cn.wc.rpc.common.bean;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * Rpc调用请求
 *
 * @author WangCong
 * @date 2019-06-18
 * @since 1.0
 */
@Data
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = -1661475201207365596L;

    private String id;

    private String methodName;

    private Class<?>[] parameterTypes = null;

    private Object[] arguments = null;

    public boolean isValid() {
        return StringUtils.isNotBlank(id)
                && StringUtils.isNotBlank(methodName);
    }
}
