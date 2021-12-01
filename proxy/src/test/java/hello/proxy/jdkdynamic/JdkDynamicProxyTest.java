package hello.proxy.jdkdynamic;

import hello.proxy.jdkdynamic.code.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@Slf4j
public class JdkDynamicProxyTest {

    /*
    주의 : AInterface 와 같이 인터페이스가 필수로 있어야한다! JDK 동적 프록시는 인터페이스를 기반으로 프록시를 동적으로 만들어준다.

    실행 순서
    1. 동적으로 생성된 프록시의 call() 호출
    2. 동적 프록시 객체는 TimeInvocationHandler.invoke() 를 호출.
    3. TimeInvokeHandler 가 공통 로직을 수행하고, method.invoke(target, args)를 호출해서 실제 객체 AImpl target 의 메서드를 호출한다.
    4. AImpl 인스턴스의 call() 실행
    5. TimeInvokeHandler 의 나머지 로직을 실행하고 결과를 반환
     */

    @Test
    void dynamicA() {
        AInterface target = new AImpl();

        //동적 프록시에 적용할 핸들러 로직, InvocationHandler 인터페이스를 구현하여 프록시 로직을 정의한다.
        InvocationHandler handler = new TimeInvocationHandler(target);

        //동적으로 프록시 객체 생성
        AInterface proxy = (AInterface)Proxy.newProxyInstance(AInterface.class.getClassLoader(), new Class[]{AInterface.class}, handler);

        proxy.call();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
    }

    @Test
    void dynamicB() {
        BInterface target = new BImpl();

        InvocationHandler handler = new TimeInvocationHandler(target);

        BInterface proxy = (BInterface)Proxy.newProxyInstance(BInterface.class.getClassLoader(), new Class[]{BInterface.class}, handler);

        proxy.call();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());
    }
}
