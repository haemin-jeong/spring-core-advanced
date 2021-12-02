package hello.proxy.cglib;

import hello.proxy.cglib.code.TimeMethodInterceptor;
import hello.proxy.common.service.ConcreteService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.Enhancer;

@Slf4j
public class CglibTest {

    @Test
    void cglib() {
        ConcreteService target = new ConcreteService();

        //CGLIB은 Enhancer를 사용해서 프록시를 생성
        Enhancer enhancer = new Enhancer();

        //ConcreteService 클래스를 상속받아 프록시 생성
        enhancer.setSuperclass(ConcreteService.class);

        //프록시에 적용할 실행 로직을 할당
        enhancer.setCallback(new TimeMethodInterceptor(target));

        ConcreteService proxy = (ConcreteService)enhancer.create(); //프록시 생성

        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.call();
    }

}
