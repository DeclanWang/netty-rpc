package client;

import com.alibaba.fastjson.util.IOUtils;
import common.bean.RpcRequest;
import common.bean.TestService;
import common.utils.JsonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public final class Client {
    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientInitializer());
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
                    request.setClassName(TestService.class.getName());
                    request.setMethodName("test");
                    request.setParameterTypes(new Class[]{String.class});
                    request.setArguments(new Object[]{"rpc test"});
                    byte[] bytes = JsonSerializer.serialize(request);

                    // Sends the received line to the server.
                    lastWriteFuture = ch.writeAndFlush(new String(bytes, IOUtils.UTF8));
                }

                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if ("bye".equals(line.toLowerCase())) {
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
