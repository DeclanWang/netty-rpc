package cn.wc.rpc.consumer;

import cn.wc.rpc.common.service.TestService;
import cn.wc.rpc.common.utils.ServiceHolder;
import cn.wc.rpc.consumer.utils.ReferenceLoader;

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
}
