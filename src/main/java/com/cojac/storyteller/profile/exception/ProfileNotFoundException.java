package com.cojac.storyteller.profile.exception;

import com.cojac.storyteller.response.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProfileNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;
}
