package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;

    /**
     * 특정 유저의 포인트 조회
     */
    public UserPoint findUserPointById(
            long userId
    ){
        return userPointTable.selectById(userId);
    }

    /**
     * 특정 유저의 포인트 충전/이용 내역을 조회
     */
    public List<PointHistory> findPointHistoriesById(
            long userId
    ){
        return pointHistoryTable.selectAllByUserId(userId);
    }

    /**
     * 특정 유저의 포인트를 충전하는 기능
     */
    public UserPoint charge(
            long userId, long amount
    ){
        // 기존에 있던 userPoint 를 가져온다
        UserPoint userPoint = userPointTable.selectById(userId);
        return userPointTable.insertOrUpdate(userPoint.id(),userPoint.point()+ amount);
    }

    /**
     * 특정 유저의 포인트를 사용하는 기능
     */
    public UserPoint use(long userId, long amount){
        // 기존에 있던 userPoint 를 가져온다
        UserPoint userPoint = userPointTable.selectById(userId);
        if(amount > userPoint.point()) throw new IllegalArgumentException("포인트 사용이 잔액보다 큽니다.");
        return userPointTable.insertOrUpdate(userPoint.id(),userPoint.point() - amount);
    }
}
