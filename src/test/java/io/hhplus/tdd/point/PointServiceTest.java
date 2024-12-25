package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointService pointService;

    @Test
    void 포인트가충전된다(){

        // given
        // UserPointTable mock = mock(UserPointTable.class);
        given(userPointTable.selectById(1L)).willReturn(new UserPoint(1L,0,System.currentTimeMillis()));

        // when
        UserPoint userPoint = pointService.charge(1L,30000);
        long chargedAmount = userPoint.point();

        // then
        Assertions.assertEquals(30000,chargedAmount);
    }

    @Test
    void 포인트를조회한다(){

        // given
        // UserPointTable mock = mock(UserPointTable.class);
        given(userPointTable.selectById(1L)).willReturn(new UserPoint(1,30000,System.currentTimeMillis()));

        // when
        UserPoint userPoint = pointService.findUserPointById(1L);

        // then
        Assertions.assertEquals(30000, userPoint.point());
    }

    @Test
    void 잔고가부족할경우사용에실패한다(){

        // given
        given(userPointTable.selectById(1L)).willReturn(new UserPoint(1,3000,System.currentTimeMillis()));

        // then
        String message = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    pointService.use(1, 4000);
                }).getMessage();

        System.out.println("resultMessage: " + message);
        Assertions.assertEquals("포인트 사용이 잔액보다 큽니다.",message);
    }

    @Test
    void 특정유저의포인트이용내역을조회한다(){

        // given
        List<PointHistory> mockList = new ArrayList<>();
        mockList.add(new PointHistory(1,1,30000,null,System.currentTimeMillis()));
        given(pointHistoryTable.selectAllByUserId(1)).willReturn(mockList);

        // when
        List<PointHistory> pointHistoriesById = pointService.findPointHistoriesById(1);

        // then
        PointHistory pointHistory = pointHistoriesById.get(0);
        Assertions.assertEquals(30000,pointHistory.amount());

    }
}
