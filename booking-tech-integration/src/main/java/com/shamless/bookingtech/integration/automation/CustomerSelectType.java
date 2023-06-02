package com.shamless.bookingtech.integration.automation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CustomerSelectType {
    ADULT("group_adults", 2),
    CHILD("group_children", 0),
    ROOM("no_rooms", 1);

    private final String forGroup;
    private final int count;
}
