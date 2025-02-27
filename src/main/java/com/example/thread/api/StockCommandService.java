package com.example.thread.api;

import com.example.thread.domain.stock.Stock;
import com.example.thread.domain.stock.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockCommandService {

    private final StockRepository stockRepository;

    public void decrease(Long id, int quantity) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당하는 재고가 없습니다."));

        stock.decrease(quantity);
    }
}
