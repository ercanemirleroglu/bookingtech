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
                .location(entity.getLocation())
                .fromDate(entity.getFromDate())
                .toDate(entity.getToDate())
                .build();
    }
}
