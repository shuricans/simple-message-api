package com.example.messageservice.service;

import com.example.messageservice.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;

public interface MessageService {

    Page<MessageDto> findMessagesByUsername(String username,
                                            Integer page,
                                            Integer size,
                                            String sortField,
                                            Direction direction);

    MessageDto saveMessage(MessageDto messageDto);
}
