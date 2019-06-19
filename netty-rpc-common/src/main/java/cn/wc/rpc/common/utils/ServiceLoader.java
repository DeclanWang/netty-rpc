package cn.wc.rpc.common.utils;

/**
 * 服务加载器
 *
 * @author WangCong
 * @date 2019-06-19
 * @since 1.0
 */
public interface ServiceLoader {

    /**
     * 从资源文件中加载服务
     *
     * @param location 存放资源文件的位置
     * @throws Exception 异常
     */
    void loadServices(String location) throws Exception;
}
