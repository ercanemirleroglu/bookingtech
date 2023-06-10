package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.domain.repository.ParamRepository;
import org.springframework.stereotype.Service;

@Service
public class ParamService {
    private final ParamRepository paramRepository;

    public ParamService(ParamRepository paramRepository) {
        this.paramRepository = paramRepository;
    }
}
