package com.shamless.bookingtech.integration.automation.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CustomerSelectType {
    ADULT("group_adults"),
    CHILD("group_children"),
    ROOM("no_rooms");

    private final String forGroup;
}
