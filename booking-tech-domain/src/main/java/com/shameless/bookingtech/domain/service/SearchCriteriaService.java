package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.domain.dto.SearchCriteriaDto;
import com.shameless.bookingtech.domain.entity.SearchCriteriaEntity;
import com.shameless.bookingtech.domain.factory.SearchCriteriaFactory;
import com.shameless.bookingtech.domain.mapper.SearchCriteriaMapper;
import com.shameless.bookingtech.domain.repository.SearchCriteriaRepository;
import org.springframework.stereotype.Service;

@Service
public class SearchCriteriaService {
    private final SearchCriteriaRepository searchCriteriaRepository;
    private final SearchCriteriaFactory searchCriteriaFactory;

    public SearchCriteriaService(SearchCriteriaRepository searchCriteriaRepository, SearchCriteriaFactory searchCriteriaFactory) {
        this.searchCriteriaRepository = searchCriteriaRepository;
        this.searchCriteriaFactory = searchCriteriaFactory;
    }

    public SearchCriteriaDto findByParams(SearchCriteriaDto searchCriteriaDto) {
        return searchCriteriaRepository.findByParamAdultAndParamChildAndParamRoomAndParamLocationAndParamCurrency(
                searchCriteriaDto.getParamAdult(),
                searchCriteriaDto.getParamChild(),
                searchCriteriaDto.getParamRoom(),
                searchCriteriaDto.getParamLocation(),
                searchCriteriaDto.getParamCurrency()
        ).map(SearchCriteriaMapper.INSTANCE::toDto).orElse(null);
    }

    public SearchCriteriaDto add(SearchCriteriaDto searchCriteriaDto) {
        SearchCriteriaEntity entityBeforeSave = searchCriteriaFactory.from(searchCriteriaDto);
        SearchCriteriaEntity entityAfterSave = searchCriteriaRepository.save(entityBeforeSave);
        return SearchCriteriaMapper.INSTANCE.toDto(entityAfterSave);
    }
}
