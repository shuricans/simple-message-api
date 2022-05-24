package com.example.messageservice.dto;

import com.example.persist.model.Message;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class MessageDto {
    private Long id;
    private String text;
    private LocalDateTime createdTime;
    private String username;

    public static MessageDto fromMessage(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .text(message.getText())
                .createdTime(message.getCreatedTime())
                .username(message.getUser().getUsername())
                .build();
    }
}
