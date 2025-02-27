package com.example.thread.api;

import com.example.thread.domain.stock.Stock;
import com.example.thread.domain.stock.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockCommandServiceTest {

    private static final Logger log = LoggerFactory.getLogger(StockCommandServiceTest.class);
    @Autowired
//    private StockCommandService stockCommandService;
    private PessimisticLockStockCommandService stockCommandService;
//    private OptimisticLockStockCommandService stockCommandService;

    @Autowired
    private StockRepository stockRepository;

    @AfterEach
    void tearDown() {
        stockRepository.deleteAllInBatch();
    }

    @DisplayName("재고는 주문수량만큼 줄어들어야한다.")
    @Test
    void decrease() throws Exception {
        // Given
        Stock stock = createStock(1L, 100);
        Stock savedStock = stockRepository.save(stock);

        // When
        stockCommandService.decrease(savedStock.getId(), 1);

        // Then
        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(1)
                .extracting("quantity")
                .containsExactly(99);
    }

    @DisplayName("동시에 100개 요청")
    @Test
    void decreaseWithThreads() throws Exception {
        // Given
        Stock stock = createStock(1L, 100);
        Stock savedStock = stockRepository.save(stock);

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        int threadCount = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);
        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockCommandService.decrease(1L, 1);
                } catch (RuntimeException e) {
                    log.error("{} 발생, 업데이트 실패", e.getClass().getSimpleName(), e);
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
                .containsExactly(0
                );
    }

    @DisplayName("동시에 100개 요청 - 실패")
    @Test
    void decreaseWithThreadsFailure() throws Exception {
        // Given
        Stock stock = createStock(1L, 100);
        Stock savedStock = stockRepository.save(stock);

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        int threadCount = 2;
        CountDownLatch latch = new CountDownLatch(threadCount);
        // When
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockCommandService.decrease(1L, 1);
                } catch (RuntimeException e) {
                    log.error("{} 발생, 업데이트 실패", e.getClass().getSimpleName(), e);
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
                .containsExactly(98
                );
    }

    private Stock createStock(Long productId, int quantity) {
        return Stock.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}