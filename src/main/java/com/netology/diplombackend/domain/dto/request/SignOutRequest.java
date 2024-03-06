package com.netology.diplombackend.domain.dto.request;

import lombok.Data;

@Data
public class SignOutRequest {
    private String authToken;
}
