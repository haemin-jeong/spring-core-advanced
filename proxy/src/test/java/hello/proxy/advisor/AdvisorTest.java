package hello.proxy.advisor;

import hello.proxy.common.advice.TimeAdvice;
import hello.proxy.common.service.ServiceImpl;
import hello.proxy.common.service.ServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Method;

/*
- Advice : 프록시가 호출하는 부가 기능 로직
- Pointcut : 부가 기능을 적용할 곳과 적용하지 않을 곳을 구분하는 필터링 로직이다. 주로 클래스와 메서드 이름으로 필터링 한다.
- Advisor : Pointcut 1개와 Advice 1개를 가지고 있다.

Advice 는 부가 기능 로직 담당, Pointcut 은 필터링 역할만 담당 함으로써 역할과 책임을 명확하게 분리
 */

@Slf4j
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

    @Test
    @DisplayName("직접 만든 포인트컷")
    void advisorTest2() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        //직접 만든 MyPointcut 적용
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(new MyPointcut(), new TimeAdvice());

        proxyFactory.addAdvisor(advisor);
        ServiceInterface proxy = (ServiceInterface)proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }

    /*
    스프링이 제공하는 포인트컷의 대표적인 몇가지 예
    - NameMatchMethodPointcut : 메서드 이름을 기반으로 매칭한다. 내부에서 PatternMatchUtils 를 사용한다(*xxx* 허용).
    - JdkRegexpMethodPointcut : JDK 정규 표현식을 기반으로 포인트컷을 매칭한다.
    - TruePointcut : 항상 true 를 반환한다.
    - AnnotationMatchingPointcut : 애노테이션으로 매칭한다.
    - AspectJExpressionPointcut : aspectJ 표현식으로 매칭한다. <- 가장 중요, 편리하고 기능이 많아 실무에서는 이것을 사용하게 된다.
     */
    @Test
    @DisplayName("스프링이 제공하는 포인트컷")
    void advisorTest3() {
        ServiceInterface target = new ServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("save");

        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, new TimeAdvice());

        proxyFactory.addAdvisor(advisor);
        ServiceInterface proxy = (ServiceInterface)proxyFactory.getProxy();

        proxy.save();
        proxy.find();
    }

    static class MyPointcut implements Pointcut {
        //클래스 필터
        @Override
        public ClassFilter getClassFilter() {
            //메서드를 기즌으로 필터를 적용할 것이기 때문에 클래스 필터는 항상 true 를 반환한도록
            return ClassFilter.TRUE;
        }

        //메서드 필터
        @Override
        public MethodMatcher getMethodMatcher() {
            return new MyMethodMatcher();
        }
    }

    static class MyMethodMatcher implements MethodMatcher {

        private final String matchName = "save";

        @Override
        public boolean matches(Method method, Class<?> targetClass) {
            //save 메서드에만 프록시 로직을 적용
            boolean result = method.getName().equals(matchName);
            log.info("포인트컷 호출 method={}, targetClass={}", method.getName(), targetClass);
            log.info("포인트컷 결과 result={}", result);
            return result;
        }

        /*
        아래 두 메서드는 무시해도 된다. 참고만 하자.
        - isRuntime()이 true 이면 우의 matches 대신 아래의 matches 가 호출된다. 아래의 matches 메서드는 동적으로 넘어오는 매개변수를 사용할 수 있다.
        - isRuntime()이 false 이면 클래스으 정적 정보만 사용하기 때문에 캐싱이 가능하지만, true 인 경우 매개변수가 동적으로 변경되기 때문에 캐싱을 하지 않는다.
         */
        @Override
        public boolean isRuntime() {
            return false;
        }

        @Override
        public boolean matches(Method method, Class<?> targetClass, Object... args) {
            return false;
        }
    }
}
