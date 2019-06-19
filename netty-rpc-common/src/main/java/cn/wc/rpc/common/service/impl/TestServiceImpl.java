package cn.wc.rpc.common.service.impl;

import cn.wc.rpc.common.service.TestService;

/**
 * 测试服务实现类
 *
 * @author WangCong
 * @since 1.0
 * @date 2019-06-19
 */
public class TestServiceImpl implements TestService {

    @Override
    public Integer test(String msg) {
        System.out.println("invoke method:test, the parameter is:" + msg);
        return 1;
    }
}
