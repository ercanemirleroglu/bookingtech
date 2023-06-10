package com.shameless.bookingtech.integration.automation.model;

import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultExtDto {
    private List<HotelPriceExtDto> hotelPriceList;
    private SearchCriteriaExtDto searchCriteria;

}
