package hello.advanced.trace.hellotrace;

import hello.advanced.trace.TraceId;
import hello.advanced.trace.TraceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HelloTraceV2 {

    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<X-";

    /**
     * - 로그 시작
     * - 로그 메시지를 파라미터로 받아 시작 로그를 출력
     * - 응답 결과로 현재 로그의 상태인 TraceStatus를 반환
     */
    public TraceStatus begin(String message) {
        TraceId traceId = new TraceId();
        long startTimeMs = System.currentTimeMillis();

        log.info("[{}] {}{}", traceId.getId(), addSpace(START_PREFIX, traceId.getLevel()), message);

        return new TraceStatus(traceId, startTimeMs, message);
    }

    /**
     * V2에서 추가
     * 기존 TraceId에서 createNextId()를 통해 다음 ID를 구한다.
     */
    public TraceStatus beginSync(TraceId beforeTraceId, String message) {
        TraceId nextId = beforeTraceId.createNextId();
        long startTimeMs = System.currentTimeMillis();

        log.info("[{}] {}{}", nextId.getId(), addSpace(START_PREFIX, nextId.getLevel()), message);

        return new TraceStatus(nextId, startTimeMs, message);
    }

    /**
     * - 정상 흐름에서 호출, 로그를 정상 종료
     * - 파라미터로 시작 로그의 상태를 전달 받고, 이 값을 활용해서 실행시간을 계산하고, 종료시에도 시작할 떄와 동일한 로그 메시지를 출력할 수 있다.
     */
    public void end(TraceStatus status) {
        complete(status, null);
    }

    /**
     * - 예외가 발생했을 때 호출, 로그를 예외 상황으로 종료
     * - TraceStatus, Exception 정보를 함께 전달 받아 실행시간, 예외 정보를 포함한 결과 로그를 출력
     */
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }

    private void complete(TraceStatus status, Exception e) {
        long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = status.getTraceId();
        if (e == null) {
            log.info("[{}] {}{} time={}ms", traceId.getId(), addSpace(COMPLETE_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs);
        } else {
            log.info("[{}] {}{} time={}ms ex={}", traceId.getId(), addSpace(EX_PREFIX, traceId.getLevel()), status.getMessage(), resultTimeMs, e.toString());
        }
    }

    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append((i == level-1) ? "|" + prefix : "|    ");
        }
        return sb.toString();
    }

}
