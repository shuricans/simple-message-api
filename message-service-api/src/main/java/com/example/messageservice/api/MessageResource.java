package com.example.messageservice.api;

import com.example.messageservice.dto.MessageDto;
import com.example.messageservice.dto.RequestMessageDto;
import com.example.messageservice.error.BadRequestException;
import com.example.messageservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/message")
public class MessageResource {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<?> fetchOrSaveMessagesByUsername(@RequestBody Optional<RequestMessageDto> requestMessageDto) {

        if (requestMessageDto.isEmpty() ||
            requestMessageDto.get().getUsername() == null ||
            requestMessageDto.get().getMessage() == null) {
            log.warn("Invalid request body.");
            throw new BadRequestException("Bad request.");
        }

        String username = requestMessageDto.get().getUsername();
        String message = requestMessageDto.get().getMessage();

        if (message.matches("^history [1-9]\\d*$")) {
            int size = Integer.parseInt(message.substring(8));
            log.info("Fetching last {} messages for user: {}", size, username);
            Page<MessageDto> page = messageService
                    .findMessagesByUsername(username, 0, size, "id", Direction.DESC);
            List<MessageDto> messages = page.getContent();
            return ResponseEntity.ok().body(messages);
        }

        MessageDto messageDto = MessageDto.builder()
                .text(message)
                .createdTime(LocalDateTime.now())
                .username(username)
                .build();

        MessageDto savedMessageDto = messageService.saveMessage(messageDto);
        log.info("Message from user: {} saved successfully", username);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/message").toUriString());
        return ResponseEntity.created(uri).body(savedMessageDto);
    }
}
