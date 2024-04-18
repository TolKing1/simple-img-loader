package org.topimg.tolking.simpleimgloader.exceptions;

import java.time.LocalDateTime;

public record ApiError (
        String path,
        String message,
        String status,
        LocalDateTime localDateTime
){}
