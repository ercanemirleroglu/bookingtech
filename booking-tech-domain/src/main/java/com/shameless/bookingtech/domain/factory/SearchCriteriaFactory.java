package com.shameless.bookingtech.domain.factory;

import com.shameless.bookingtech.domain.dto.SearchCriteriaDto;
import com.shameless.bookingtech.domain.entity.SearchCriteriaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchCriteriaFactory {
    public SearchCriteriaEntity from(SearchCriteriaDto dto) {
        return SearchCriteriaEntity.builder()
                .paramAdult(dto.getParamAdult())
                .paramChild(dto.getParamChild())
                .paramRoom(dto.getParamRoom())
                .location(dto.getLocation())
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .build();
    }
}
