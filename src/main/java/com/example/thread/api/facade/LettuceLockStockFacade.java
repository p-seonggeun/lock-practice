package com.example.thread.api.facade;

import com.example.thread.api.StockCommandService;
import com.example.thread.util.lock.RedisLockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LettuceLockStockFacade {

    private final RedisLockRepository redisLockRepository;
    private final StockCommandService stockCommandService;

    public void decrease(Long id, int quantity) throws InterruptedException {
        while (!redisLockRepository.lock(id)) {
            log.info("lock 획득 실패");
            Thread.sleep(100);
        }

        log.info("lock 획득");
        try {
            stockCommandService.decrease(id, quantity);
        } finally {
            redisLockRepository.unlock(id);
            log.info("lock 해제");
        }
    }
}
