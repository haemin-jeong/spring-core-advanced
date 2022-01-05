package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/*
- Advice : 프록시가 호출하는 부가 기능 로직
- Pointcut : 부가 기능을 적용할 곳과 적용하지 않을 곳을 구분하는 필터링 로직이다. 주로 클래스와 메서드 이름으로 필터링 한다.
- Advisor : Pointcut 1개와 Advice 1개를 가지고 있다.

Advice 는 부가 기능 로직 담당, Pointcut 은 필터링 역할만 담당 함으로써 역할과 책임을 명확하게 분리
 */

public class AdvisorTest {

    @Test
    void advisorTest1() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        /*
        - DefaultPointcutAdvisor 는 Advisor 인터페이스의 일반적인 구현체
        - Pointcut.TRUE : 항상 true 를 반환하는 포인트컷

        참고 : addAdvice() 메서드도 단순 편의 메서드이고, 내부를 들여다보면 결과적으로 DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice())와 같은 Advisor가 생성된다.
        */
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(Pointcut.TRUE, new TimeAdvice());
        proxyFactory.addAdvisor(advisor);
        ServiceInterface proxy = (ServiceInterface)proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }
}
