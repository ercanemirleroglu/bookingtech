package com.shameless.bookingtech.domain.mapper;

import com.shameless.bookingtech.domain.dto.SearchCriteriaDto;
import com.shameless.bookingtech.domain.entity.SearchCriteriaEntity;

public class SearchCriteriaMapper {
    public static final SearchCriteriaMapper INSTANCE = new SearchCriteriaMapper();

    public SearchCriteriaDto toDto(SearchCriteriaEntity entity) {
        return SearchCriteriaDto.builder()
                .id(entity.getId())
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .paramAdult(entity.getParamAdult())
                .paramChild(entity.getParamChild())
                .paramRoom(entity.getParamRoom())
                .paramLocation(entity.getParamLocation())
                .paramCurrency(entity.getParamCurrency())
                .dayRange(entity.getDayRange())
                .build();
    }
}
