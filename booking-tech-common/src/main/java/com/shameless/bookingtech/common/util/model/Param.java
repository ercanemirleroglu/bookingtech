package com.shameless.bookingtech.common.util.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Param {
    APP_CURRENCY_UNIT(false),
    APP_LANGUAGE(false),
    SEARCH_LOCATION(false),
    SEARCH_DATE_RANGE(false),
    SEARCH_ADULT(true),
    SEARCH_CHILD(true),
    SEARCH_ROOM(true),
    EMAIL_TO(false);

    private final boolean customerSelect;

    public boolean isCustomerSelect(){
        return this.customerSelect;
    }
}
