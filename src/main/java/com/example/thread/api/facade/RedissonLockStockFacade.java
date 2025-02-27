package com.example.thread.api.facade;

import com.example.thread.api.StockCommandService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedissonLockStockFacade {

    private final RedissonClient redissonClient;
    private final StockCommandService stockCommandService;

    public RedissonLockStockFacade(RedissonClient redissonClient, StockCommandService stockCommandService) {
        this.redissonClient = redissonClient;
        this.stockCommandService = stockCommandService;
    }

    public void decrease(Long id, int quantity) {
        RLock lock = redissonClient.getLock(id.toString());

        try {
            boolean available = lock.tryLock(
                    10, // lock 획득을 위한 대기 시간
                    5, // lock 점유 시간
                    TimeUnit.SECONDS
            );

            if (!available) {
                log.info("lock 획득 실패");
                return;
            }

            stockCommandService.decrease(id, quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
