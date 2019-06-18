package server;

import com.alibaba.fastjson.util.IOUtils;
import common.bean.RpcRequest;
import common.bean.RpcResponse;
import common.utils.JsonSerializer;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * Rpc服务端处理器
 *
 * @author WangCong
 * @date 2019-06-18
 * @since 1.0
 */
@ChannelHandler.Sharable
public class RpcServerHandler extends SimpleChannelInboundHandler<String> {


    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        RpcResponse response = new RpcResponse();
        if (StringUtils.isBlank(msg)) {
            response.setCode(0);
            response.setMsg("invalid request!");
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            if ("bye".equals(msg)) {
                ctx.close().sync();
                return;
            }
            byte[] bytes = msg.getBytes(IOUtils.UTF8);
            RpcRequest request = JsonSerializer.deserialize(bytes, RpcRequest.class);
            if (request.isValid()) {
                String className = request.getClassName();
                try {
                    Class<?> clazz = Class.forName(className);
                    Object o = clazz.newInstance();
                    Class<?>[] parameterTypes = request.getParameterTypes();
                    Object[] arguments = request.getArguments();
                    Method method;
                    if (parameterTypes != null && parameterTypes.length > 0) {
                        method = clazz.getMethod(request.getMethodName(), parameterTypes);
                    } else {
                        method = clazz.getMethod(request.getMethodName());
                    }
                    Object result;
                    if (arguments != null && arguments.length > 0) {
                        result = method.invoke(o, arguments);
                    } else {
                        result = method.invoke(o);
                    }
                    response.setData(result);
                    response.setMsg("success!");

                    String out = new String(JsonSerializer.serialize(response), IOUtils.UTF8);
                    ctx.writeAndFlush(out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                response.setCode(0);
                response.setMsg("invalid request!");
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
}
