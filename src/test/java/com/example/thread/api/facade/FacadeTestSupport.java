package com.example.thread.api.facade;

import com.example.thread.domain.stock.Stock;
import com.example.thread.domain.stock.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public abstract class FacadeTestSupport {

    @AfterEach
    void tearDown() {
        stockRepository.deleteAllInBatch();
    }

    @Autowired
    protected RedissonLockStockFacade redissonLockStockFacade;

    @Autowired
    protected LettuceLockStockFacade lettuceLockStockFacade;

    @Autowired
    protected OptimisticLockStockFacade optimisticLockStockFacade;

    @Autowired
    protected NamedLockStockFacade namedLockStockFacade;

    @Autowired
    protected StockRepository stockRepository;

    protected Stock createStock(Long productId, int quantity) {
        return Stock.builder()
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}
