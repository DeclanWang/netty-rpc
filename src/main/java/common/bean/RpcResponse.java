package common.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * Rpc调用响应
 *
 * @author WangCong
 * @date 2019-06-018
 * @since 1.0
 */
@Data
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = -5362635397914916310L;

    private int code = 1;

    private Object data;

    private String msg = null;
}
