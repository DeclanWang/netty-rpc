package cn.wc.rpc.provider.utils;

import cn.wc.rpc.common.utils.ServiceHolder;
import cn.wc.rpc.common.utils.ServiceLoader;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * 服务提供者加载器
 *
 * @author WangCong
 * @since 2019-06-19
 */
@Slf4j
public class ProviderLoader implements ServiceLoader {

    @Override
    public void loadServices(String location) throws Exception {
        // 加载 xml 配置文件
        InputStream inputStream = new FileInputStream(location);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(inputStream);
        Element root = doc.getDocumentElement();
        NodeList nodes = root.getChildNodes();

        // 遍历 <cn.wc.rpc.service> 标签
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                Element ele = (Element) node;
                String id = ele.getAttribute("id");
                String className = ele.getAttribute("class");

                // 加载 beanClass
                Class beanClass;
                try {
                    beanClass = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    log.info("load provider error, the detail is:", e);
                    return;
                }

                // 创建service
                Object service = beanClass.newInstance();

                ServiceHolder.addService(id, service);
            }
        }
    }
}
