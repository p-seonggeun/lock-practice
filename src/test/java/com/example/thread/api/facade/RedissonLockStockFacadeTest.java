package com.example.thread.api.facade;

import com.example.thread.domain.stock.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

class RedissonLockStockFacadeTest extends FacadeTestSupport {

    @DisplayName("동시에 100개 요청")
    @Test
    void decreaseWithThreads() throws Exception {
        // Given
        Stock stock = createStock(1L, 100);
        Stock savedStock = stockRepository.save(stock);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    redissonLockStockFacade.decrease(1L, 1);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // Then
        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(1)
                .extracting("quantity")
                .containsExactly(0)
        ;
    }
}