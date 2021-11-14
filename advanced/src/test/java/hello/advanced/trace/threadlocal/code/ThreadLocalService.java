package hello.advanced.trace.threadlocal.code;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadLocalService {

    /*
    ThreadLocal
    - 저장 : ThreadLocal.set()
    - 조회 : ThreadLocal.get()
    - 제거 : ThreadLocal.remove(), 해당 쓰레드가 ThreadLocal을 모두 사용하고나면 꼭 제거를 해줘야한다.
     */
    private ThreadLocal<String> nameStore = new ThreadLocal<>();

    public String logic(String name) {
        log.info("저장 name={} -> nameStore={}", name, nameStore.get());
        nameStore.set(name);
        sleep(1000);
        log.info("조회 nameStore={}", nameStore.get());
        return nameStore.get();
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
