package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PointControllerTest {

    @Autowired
    private UserPointTable userPointTable;

    @Autowired
    private PointService pointService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 특정유저의포인트를조회() throws Exception {
        //given
        userPointTable.insertOrUpdate(1L, 1000L);

        System.out.print(userPointTable.selectById(1L));

        long userId = 1L;


        // when
        // then
        mockMvc.perform(get("/point/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(1000));
    }

    @Test
    void 동시성제어테스트() throws InterruptedException {

        // given

        // 초기 포인트 데이터 삽입
        userPointTable.insertOrUpdate(1L, 1000L);
        // threads 수 설정
        int threads = 10;
        // ExecutorService로 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        // 모든 스레드가 종료되기까지 기다릴 latch
        CountDownLatch latch = new CountDownLatch(threads);

        // when

        // 각 스레드에서 충전할 금액
        long chargeAmountPerThread = 500;
        for (int i = 0; i < threads; i++) {
            executorService.submit(()->{
                try {
                    pointService.charge(1, chargeAmountPerThread);
                } finally {
                    latch.countDown();  // 작업 완료 후 카운트다운
                }
            });
        }

        // 모든 스레드가 완료될 때 까지 기다린다.
        latch.await();
        // Executor 종료
        executorService.shutdown();

        // then

        // 최종 유저 포인트
        UserPoint userPoint = userPointTable.selectById(1L);
        // 초기 값 1000 + (10개의 스레드 * 500)
        long expectedFinalPoint = 1000 + (threads * chargeAmountPerThread);
        System.out.println("userPoint : " + userPoint.point());
        Assertions.assertEquals(expectedFinalPoint, userPoint.point());

    }
}
