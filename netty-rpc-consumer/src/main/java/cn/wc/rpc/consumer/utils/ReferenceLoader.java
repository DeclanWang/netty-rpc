package cn.wc.rpc.consumer.utils;


import cn.wc.rpc.common.bean.RpcRequest;
import cn.wc.rpc.common.utils.JsonSerializer;
import cn.wc.rpc.common.utils.ServiceHolder;
import cn.wc.rpc.common.utils.ServiceLoader;
import cn.wc.rpc.consumer.RpcClientInitializer;
import com.alibaba.fastjson.util.IOUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 服务引用者加载器
 *
 * @author WangCong
 * @date 2019-06-19
 * @since 1.0
 */
@Slf4j
public class ReferenceLoader implements ServiceLoader {

    private final static String HOST = "127.0.0.1";
    private final static int PORT = 8888;
    private final static int LOOP = 100;

    @Override
    public void loadServices(String location) throws Exception {
        // 加载 xml 配置文件
        InputStream inputStream = new FileInputStream(location);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(inputStream);
        Element root = doc.getDocumentElement();
        NodeList nodes = root.getChildNodes();

        // 遍历 <reference> 标签
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                Element ele = (Element) node;
                String id = ele.getAttribute("id");
                String interfaceName = ele.getAttribute("interface");

                // 加载 beanClass
                Class interfaceClass;
                try {
                    interfaceClass = Class.forName(interfaceName);
                } catch (ClassNotFoundException e) {
                    log.error("load reference error, the detail is:", e);
                    return;
                }

                // 创建代理
                Object service = Proxy.newProxyInstance(ReferenceLoader.class.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Bootstrap b = new Bootstrap();
                        b.group(new NioEventLoopGroup())
                                .channel(NioSocketChannel.class)
                                .handler(new RpcClientInitializer());
                        Channel ch = b.connect(HOST, PORT).sync().channel();

                        RpcRequest request = new RpcRequest();
                        request.setId(id);
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setArguments(args);
                        byte[] bytes = JsonSerializer.serialize(request);

                        ch.writeAndFlush(new String(bytes, IOUtils.UTF8)).sync();

                        Object result = null;
                        ChannelId channelId = ch.id();
                        // 自旋获取结果
                        for (int i = 0; i < LOOP; i++) {
                            if (ResultKeeper.has(channelId)) {
                                result = ResultKeeper.get(channelId);
                            }
                        }

                        return result;
                    }
                });

                ServiceHolder.addService(id, service);
            }
        }
    }
}
