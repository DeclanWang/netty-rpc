package common.bean;

public class TestService {

    public Integer test(String msg) {
        System.out.println("invoke method:test, the parameter is:" + msg);
        return 1;
    }
}
