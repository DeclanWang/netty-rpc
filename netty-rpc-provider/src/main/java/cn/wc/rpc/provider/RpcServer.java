package cn.wc.rpc.provider;

import cn.wc.rpc.provider.utils.ProviderLoader;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.URL;

/**
 * RPC服务器
 *
 * @author WangCong
 * @date 2019-06-18
 * @since 1.0
 */
public class RpcServer {
    private static final int PORT = 8888;

    public static void main(String[] args) throws Exception {
        // 加载服务
        loadServices();
        // 启动服务器
        initServer();
    }

    private static void loadServices() throws Exception {
        URL resource = RpcServer.class.getClassLoader().getResource("provider.xml");
        if (resource != null) {
            String location = resource.getFile();
            ProviderLoader loader = new ProviderLoader();
            loader.loadServices(location);
        }
    }

    private static void initServer() throws InterruptedException {
        // 创建boss线程组 用于服务端接受客户端的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 创建 worker 线程组 用于进行 SocketChannel 的数据读写
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.group(bossGroup, workGroup)
                    // 设置要被实例化的为 NioServerSocketChannel 类
                    .channel(NioServerSocketChannel.class)
                    // 设置 NioServerSocketChannel 的处理器
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 设置连入服务端的 Client 的 SocketChannel 的处理器
                    .childHandler(new RpcServerInitializer());
            // 绑定端口，并同步等待成功，即启动服务端
            Channel channel = bootstrap.bind(PORT).sync().channel();
            channel.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
