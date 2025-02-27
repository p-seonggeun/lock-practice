package com.example.thread.api;

import com.example.thread.domain.stock.Stock;
import com.example.thread.domain.stock.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PessimisticLockStockCommandService {

    private final StockRepository stockRepository;

    public void decrease(Long id, int quantity) {
        Stock stock = stockRepository.findByIdWithPessimisticLock(id);

        stock.decrease(quantity);
    }
}
