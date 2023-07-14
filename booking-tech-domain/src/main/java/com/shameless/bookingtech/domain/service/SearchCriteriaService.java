package com.shameless.bookingtech.domain.service;

import com.shameless.bookingtech.common.util.model.Param;
import com.shameless.bookingtech.domain.dto.ParamDto;
import com.shameless.bookingtech.domain.dto.SearchCriteriaDto;
import com.shameless.bookingtech.domain.entity.SearchCriteriaEntity;
import com.shameless.bookingtech.domain.factory.SearchCriteriaFactory;
import com.shameless.bookingtech.domain.mapper.SearchCriteriaMapper;
import com.shameless.bookingtech.domain.repository.SearchCriteriaRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchCriteriaService {
    private final SearchCriteriaRepository searchCriteriaRepository;
    private final SearchCriteriaFactory searchCriteriaFactory;
    private final ParamService paramService;

    public SearchCriteriaService(SearchCriteriaRepository searchCriteriaRepository, SearchCriteriaFactory searchCriteriaFactory, ParamService paramService) {
        this.searchCriteriaRepository = searchCriteriaRepository;
        this.searchCriteriaFactory = searchCriteriaFactory;
        this.paramService = paramService;
    }

    public SearchCriteriaDto findByParams(SearchCriteriaDto searchCriteriaDto) {
        return searchCriteriaRepository.findBySearchParams(
                searchCriteriaDto.getDayRange(),
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

    public SearchCriteriaDto getCriteriaByParams() {
        List<ParamDto> searchParamList = paramService.getSearchParams();
        Map<Param, String> params = new HashMap<>();
        searchParamList.forEach(param -> params.put(param.getKey(), param.getValue()));
        return searchCriteriaRepository.findBySearchParams(
                        Integer.parseInt(params.get(Param.SEARCH_DATE_RANGE)),
                        Integer.parseInt(params.get(Param.SEARCH_ADULT)),
                        Integer.parseInt(params.get(Param.SEARCH_CHILD)),
                        Integer.parseInt(params.get(Param.SEARCH_ROOM)),
                        params.get(Param.SEARCH_LOCATION),
                        params.get(Param.APP_CURRENCY_UNIT))
                .map(SearchCriteriaMapper.INSTANCE::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Search Criteria " +
                        "record not found by search params"));
    }
}
