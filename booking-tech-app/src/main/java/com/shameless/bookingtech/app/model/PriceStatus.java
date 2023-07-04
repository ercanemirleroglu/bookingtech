package com.shameless.bookingtech.app.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PriceStatus {
    INCREASED("#a6ffb5"),
    DECREASED("#ffb5b5"),
    STATIC(null),
    NEW(null);

    private final String backgroundColor;
}
