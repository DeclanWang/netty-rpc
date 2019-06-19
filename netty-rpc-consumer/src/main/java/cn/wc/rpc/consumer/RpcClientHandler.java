package cn.wc.rpc.consumer;

import cn.wc.rpc.common.bean.RpcResponse;
import cn.wc.rpc.common.utils.JsonSerializer;
import cn.wc.rpc.consumer.utils.ResultKeeper;
import com.alibaba.fastjson.util.IOUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Rpc客户端处理器
 *
 * @author WangCong
 * @date 2019-06-19
 * @since 1.0
 */
@ChannelHandler.Sharable
public class RpcClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        RpcResponse response = (RpcResponse) JsonSerializer.deserialize(msg.getBytes(IOUtils.UTF8));
        Integer result = (Integer) response.getData();
        System.out.println(result);
        ResultKeeper.put(ctx.channel().id(), result);
        ctx.close().sync();
    }
}
