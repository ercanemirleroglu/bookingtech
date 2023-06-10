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
        return searchCriteriaRepository.findByParamAdultAndParamChildAndParamRoomAndLocation(
                searchCriteriaDto.getParamAdult(),
                searchCriteriaDto.getParamChild(),
                searchCriteriaDto.getParamRoom(),
                searchCriteriaDto.getLocation()
        ).map(SearchCriteriaMapper.INSTANCE::toDto).orElse(null);
    }

    public SearchCriteriaDto add(SearchCriteriaDto searchCriteriaDto) {
        SearchCriteriaEntity entityBeforeSave = searchCriteriaFactory.from(searchCriteriaDto);
        SearchCriteriaEntity entityAfterSave = searchCriteriaRepository.save(entityBeforeSave);
        return SearchCriteriaMapper.INSTANCE.toDto(entityAfterSave);
    }

    public SearchCriteriaDto update(SearchCriteriaDto searchCriteriaDto) {
        SearchCriteriaEntity searchCriteriaEntity = searchCriteriaRepository.findById(searchCriteriaDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Search Criteria not found! Id: "
                        + searchCriteriaDto.getId()));
        searchCriteriaEntity.update(searchCriteriaDto);
        SearchCriteriaEntity save = searchCriteriaRepository.save(searchCriteriaEntity);
        return SearchCriteriaMapper.INSTANCE.toDto(save);
    }
}
