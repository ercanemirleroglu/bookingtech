package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.domain.dto.ParamDto;
import com.shameless.bookingtech.domain.mapper.ParamMapper;
import com.shameless.bookingtech.domain.repository.ParamRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParamService {
    private final ParamRepository paramRepository;

    public ParamService(ParamRepository paramRepository) {
        this.paramRepository = paramRepository;
    }

    public List<ParamDto> getAllParams() {
        return paramRepository.findAll().stream().map(ParamMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }
}
