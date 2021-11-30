package hello.proxy.jdkdynamic;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

@Slf4j
public class ReflectionTest {

    /**
     * 리플렉션 미사용
     */
    @Test
    void reflection0() {
        Hello target = new Hello();

        //공통 로직1 시작
        log.info("start");
        String result1 = target.callA(); //호출하는 메서드가 다름
        log.info("result={}", result1);
        //공통 로직1 종료

        //공통 로직2 시작
        log.info("start");
        String result2 = target.callB (); //호출하는 메서드가 다름
        log.info("result={}", result1);
        //공통 로직2 종료
    }

    /**
     * 리플렉션 사용
     */
    @Test
    void reflection1() throws Exception {
        //클래스 메타 정보(내부 클래스는 $를 사용해서 구분)
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();

        //callA 메서드 정보
        Method methodCallA = classHello.getMethod("callA");

        //target 인스턴스의 callA 호출
        Object result1 = methodCallA.invoke(target);
        log.info("result1={}", result1);

        //callB 메서드 정보
        Method methodCallB = classHello.getMethod("callB");
        Object result2 = methodCallB.invoke(target); //target 인스턴스의 callA 호출
        log.info("result1={}", result2);
    }

    /**
     * 메서드 호출을 공통 로직(dynamicCall 메서드)으로 분리
     * 정적인 target.callA()와 target.callB()를 리플렉션을 사용하여 Method라는 메타 정보로 추상화하여 메서드 호출을 공통 로직으로 처리
     */
    @Test
    void reflection2() throws Exception {
        //클래스 정보(내부 클래스는 $)
        Class classHello = Class.forName("hello.proxy.jdkdynamic.ReflectionTest$Hello");

        Hello target = new Hello();
        //callA 메서드 정보
        Method methodCallA = classHello.getMethod("callA");
        dynamicCall(methodCallA, target);

        //callA 메서드 정보
        Method methodCallB = classHello.getMethod("callB");
        dynamicCall(methodCallB, target);
    }

    private void dynamicCall(Method method, Object target) throws Exception {
        log.info("start");
        Object result = method.invoke(target);
        log.info("result1={}", result);
    }

    @Slf4j
    static class Hello {

        public String callA() {
            log.info("callA");
            return "A";
        }
        public String callB() {
            log.info("callB");
            return "B";
        }
    }
}
