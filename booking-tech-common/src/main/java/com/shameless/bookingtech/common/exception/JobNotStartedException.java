package com.shameless.bookingtech.common.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JobNotStartedException extends RuntimeException {

    public JobNotStartedException(String message) {
        super(message);
    }
}
