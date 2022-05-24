package com.example.messageservice.error;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiErrorResponse {
    private String error;
    private String message;
}
