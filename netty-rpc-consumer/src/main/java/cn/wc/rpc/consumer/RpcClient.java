package cn.wc.rpc.consumer;

import cn.wc.rpc.common.bean.RpcRequest;
import cn.wc.rpc.common.constants.RpcConstant;
import cn.wc.rpc.common.service.TestService;
import cn.wc.rpc.common.utils.JsonSerializer;
import cn.wc.rpc.common.utils.ServiceHolder;
import cn.wc.rpc.consumer.utils.ReferenceLoader;
import com.alibaba.fastjson.util.IOUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Rpc客户端
 *
 * @author WangCong
 * @date 2019-06-19
 * @since 1.0
 */
public final class RpcClient {
    public static void main(String[] args) throws Exception {
        // 加载服务引用
        loadServices();

        TestService testService = ServiceHolder.getService("testService", TestService.class);
        if (testService != null) {
            testService.test("apple");
        }

        for (; ; ) {
        }
    }

    private static void loadServices() throws Exception {
        URL resource = RpcClient.class.getClassLoader().getResource("consumer.xml");
        if (resource != null) {
            String location = resource.getFile();
            ReferenceLoader loader = new ReferenceLoader();
            loader.loadServices(location);
        }
    }

    /**
     * 测试使用
     */
    private static void initClient() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new RpcClientInitializer());
            Channel ch = b.connect("127.0.0.1", 8888).sync().channel();

            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (; ; ) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }

                if ("invoke".equals(line.toLowerCase())) {
                    RpcRequest request = new RpcRequest();
                    request.setId("testService");
                    request.setMethodName("test");
                    request.setParameterTypes(new Class[]{String.class});
                    request.setArguments(new Object[]{"rpc test"});
                    byte[] bytes = JsonSerializer.serialize(request);

                    // Sends the received line to the server.
                    lastWriteFuture = ch.writeAndFlush(new String(bytes, IOUtils.UTF8));
                }

                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if (RpcConstant.BYE.equals(line.toLowerCase())) {
                    lastWriteFuture = ch.writeAndFlush(line);
                    ch.closeFuture().sync();
                    break;
                }
            }

            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } finally {
            group.shutdownGracefully();
        }
    }
}
