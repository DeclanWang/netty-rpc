package client;


import com.alibaba.fastjson.util.IOUtils;
import common.bean.RpcResponse;
import common.utils.JsonSerializer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        RpcResponse response = (RpcResponse) JsonSerializer.deserialize(msg.getBytes(IOUtils.UTF8));
        Integer result = (Integer) response.getData();
        System.out.println(result);
    }
}
