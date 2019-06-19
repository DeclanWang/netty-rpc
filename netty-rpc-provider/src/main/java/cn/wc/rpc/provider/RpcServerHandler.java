package cn.wc.rpc.provider;

import cn.wc.rpc.common.bean.RpcRequest;
import cn.wc.rpc.common.bean.RpcResponse;
import cn.wc.rpc.common.constants.RpcConstant;
import cn.wc.rpc.common.utils.JsonSerializer;
import cn.wc.rpc.common.utils.ServiceHolder;
import com.alibaba.fastjson.util.IOUtils;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Rpc服务端处理器
 *
 * @author WangCong
 * @date 2019-06-18
 * @since 1.0
 */
@Slf4j
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
            if (RpcConstant.BYE.equals(msg)) {
                ctx.close().sync();
                return;
            }
            byte[] bytes = msg.getBytes(IOUtils.UTF8);
            RpcRequest request = JsonSerializer.deserialize(bytes, RpcRequest.class);
            if (request.isValid()) {
                try {
                    String id = request.getId();
                    Object o = ServiceHolder.getService(id);
                    if (Objects.nonNull(o)) {
                        Class<?> clazz = o.getClass();
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
                        ctx.writeAndFlush(out).addListener(ChannelFutureListener.CLOSE);
                    } else {
                        response.setCode(0);
                        response.setMsg("service not found!");
                        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                    }
                } catch (Exception e) {
                    log.error("received message from client error, the detail is:", e);
                }
            } else {
                response.setCode(0);
                response.setMsg("invalid request!");
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
}
