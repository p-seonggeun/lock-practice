package com.example.thread.api.facade;

import com.example.thread.api.OptimisticLockStockCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OptimisticLockStockFacade {

    private final OptimisticLockStockCommandService optimisticLockStockCommandService;

    public void decrease(Long id, int quantity) throws InterruptedException {
        while (true) {
            try {
                optimisticLockStockCommandService.decrease(id, quantity);
                break;
            } catch (ObjectOptimisticLockingFailureException e) {
                log.error("{} 발생, 업데이트 실패", e.getClass().getSimpleName(), e);
                Thread.sleep(50);
            }
        }
    }

}
