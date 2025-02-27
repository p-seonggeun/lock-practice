package com.example.thread.api.facade;

import com.example.thread.api.StockCommandService;
import com.example.thread.domain.stock.LockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class NamedLockStockFacade {

    private final LockRepository lockRepository;
    private final StockCommandService stockCommandService;

    public void decrease(Long id, int quantity) {
        try {
            lockRepository.getLock(id.toString());

            stockCommandService.decrease(id, quantity);
        } finally {
            lockRepository.releaseLock(id.toString());
        }
    }
}
