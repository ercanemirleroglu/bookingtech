package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.domain.repository.PriceRepository;
import org.springframework.stereotype.Service;

@Service
public class PriceService {
    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }
}
