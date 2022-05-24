package com.example.messageservice.service;

import com.example.messageservice.dto.MessageDto;
import com.example.persist.model.Message;
import com.example.persist.repository.MessageRepository;
import com.example.persist.repository.UserRepository;
import com.example.persist.specification.MessageSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public Page<MessageDto> findMessagesByUsername(String username,
                                                   Integer page,
                                                   Integer size,
                                                   String sortField,
                                                   Direction direction) {
        Specification<Message> spec = Specification
                .where(MessageSpecification.usernameLike(username));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortField));

        return messageRepository.findAll(spec, pageRequest)
                .map(MessageDto::fromMessage);
    }

    @Override
    public MessageDto saveMessage(MessageDto messageDto) {
        Message message = new Message();
        message.setText(messageDto.getText());
        message.setCreatedTime(messageDto.getCreatedTime());
        message.setUser(userRepository.findByUsername(messageDto.getUsername()).orElseGet(null));
        message = messageRepository.save(message);
        return MessageDto.fromMessage(message);
    }
}
