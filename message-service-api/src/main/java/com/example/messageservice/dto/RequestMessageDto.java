package com.example.messageservice.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RequestMessageDto {
    private String username;
    private String message;
}
