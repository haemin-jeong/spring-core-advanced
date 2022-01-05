package hello.proxy.proxyfactory;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ConcreteService;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

import static org.assertj.core.api.Assertions.assertThat;

/*
프록시 팩토리를 사용해서 편리하게 동적 프록시 객체를 생성할 수 있다.
- 프록시 팩토리는 인터페이스가 있는 경우 JDK 동적 프록시, 구체 클래스만 있는 경우 CGLIB 을 사용해서 프록시 객체를 만들어준다.
- 프록시의 부가 기능 로직도 특정 기술에 종속적이지 않게 Advice 하나로 사용할 수 있다.
- 프록시 팩토리는 내부에서 JDK 동적 프록시인 경우 InvocationHandler 가 Advice 를 호출, CGLIB 인 경우 MethodInterceptor 가 Advice 를 호출한다.
 */

@Slf4j
public class ProxyFactoryTest {

    @Test
    @DisplayName("인터페이스가 있으면 JDK 동적 프록시 사용")
    void interfaceProxy() {
        ServiceInterface target = new ServiceImpl();

        ProxyFactory proxyFactory = new ProxyFactory(target);

        //Advice는 부가 기능 로직(JDK 동적 프록시의 InvocationHandler, cglib의 MethodInterCeptor와 유사)
        proxyFactory.addAdvice(new TimeAdvice());

        ServiceInterface proxy = (ServiceInterface)proxyFactory.getProxy();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.save();

        //해당 메서드들은 직접 JDK 동적 프록시, CGLib을 사용해서 프록시를 생성할 경우엔 사용할 수 없고
        //프록시팩토리를 사용해서 프록시를 생성한 경우에만 사용할 수 있다.
        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isTrue();
        assertThat(AopUtils.isCglibProxy(proxy)).isFalse();
    }

    @Test
    @DisplayName("구체 클래스만 있으면 CGLIB 사용")
    void concreteProxy() {
        ConcreteService target = new ConcreteService();

        ProxyFactory proxyFactory = new ProxyFactory(target);

        proxyFactory.addAdvice(new TimeAdvice());

        ConcreteService proxy = (ConcreteService)proxyFactory.getProxy();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.call();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }

    @Test
    @DisplayName("proxyTargetClass 옵션을 사용하면 인터페이스가 있어도 CGLIB을 사용하고, 클래스 기반 프록시 사용")
    void proxyTargetClass() {
        ServiceInterface target = new ServiceImpl();

        ProxyFactory proxyFactory = new ProxyFactory(target);

        //인터페이스가 있어도 클래스 기반 프록시를 생성, ServiceImpl을 사용하여 프록시 생성
        //중요한 옵션! 실무에서 종종 등장하는 옵션이다.
        proxyFactory.setProxyTargetClass(true);

        proxyFactory.addAdvice(new TimeAdvice());

        ServiceInterface proxy = (ServiceInterface)proxyFactory.getProxy();
        log.info("targetClass={}", target.getClass());
        log.info("proxyClass={}", proxy.getClass());

        proxy.save();

        assertThat(AopUtils.isAopProxy(proxy)).isTrue();
        assertThat(AopUtils.isJdkDynamicProxy(proxy)).isFalse();
        assertThat(AopUtils.isCglibProxy(proxy)).isTrue();
    }
}
