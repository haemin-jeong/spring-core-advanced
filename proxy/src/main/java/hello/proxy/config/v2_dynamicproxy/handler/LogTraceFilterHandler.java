package hello.proxy.config.v2_dynamicproxy.handler;

import hello.proxy.trace.TraceStatus;
import hello.proxy.trace.logtrace.LogTrace;
import org.springframework.util.PatternMatchUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LogTraceFilterHandler implements InvocationHandler {

    private final Object target;
    private final LogTrace logTrace;
    private final String[] patterns;

    public LogTraceFilterHandler(Object target, LogTrace logTrace, String[] patterns) {
        this.target = target;
        this.logTrace = logTrace;
        this.patterns = patterns;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //메서드 이름 필터
        String methodName = method.getName();

        /*
        PatternMatchUtils.simpleMatch()
        - xxx : xxx가 정확히 매칭되면 참
        - xxx* : xxx로 시작하면 참
        - *xxx : xxx로 끝나면 참
        - *xxx* : xxx가 있으면 참
         */
        if (!PatternMatchUtils.simpleMatch(patterns, methodName)) {
            //메서드 이름이 주입 받은 패턴과 일치하지 않는 경우 로그를 남기지 않고 결과만 반환
            return method.invoke(target, args);
        }

        TraceStatus status = null;

        try {
            String message = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()";
            status = logTrace.begin(message);
            Object result = method.invoke(target, args);
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
