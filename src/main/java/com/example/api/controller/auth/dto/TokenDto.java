package com.example.api.controller.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenDto {
    private String accessToken;
    private String reflashToken;

    @Builder
    public TokenDto(String accessToken, String reflashToken) {
        this.accessToken = accessToken;
        this.reflashToken = reflashToken;
    }
}