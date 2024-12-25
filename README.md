# 동시성 제어 방식에 대한 분석 
-- --
Application level 에서 동시성을 제어할 수 있는 두가지 방식

## synchronized vs ReentrantLock


### synchronized의 동작 방식

`synchronized 키워드가 붙은 메서드는 해당 객체 수준에서 모니터 락(Monitor Lock)을 사용 하나의 스레드가 락을 획득하고 메서드를 실행하는 동안, 다른 스레드들은 대기 상태, 락은 메서드 실행이 끝난 후 자동으로 해제`

### ReentrantLock의 동작 방식

`lock.lock()으로 임계영역을 잠그고, 작업이 끝난 후 반드시 lock.unlock()로 잠금을 해제, 예외가 발생하더라도 finally 블록을 통해 안전하게 잠금을 해제
Wrapper 클래스를 사용하여 기존 구현을 변경하지 않을 수 있음, 조건 변수나 대기/알림 메커니즘을 활용할 수 있어 확장성이 뛰어남`

