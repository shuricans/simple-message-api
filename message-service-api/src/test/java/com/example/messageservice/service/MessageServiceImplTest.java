package com.example.messageservice.service;

import com.example.messageservice.dto.MessageDto;
import com.example.messageservice.error.BlankMessageException;
import com.example.messageservice.error.UserNotFoundException;
import com.example.persist.model.Message;
import com.example.persist.model.User;
import com.example.persist.repository.MessageRepository;
import com.example.persist.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;

    private MessageService underTest;

    @BeforeEach
    void setUp() {
        underTest = new MessageServiceImpl(messageRepository, userRepository);
    }

    @Test
    void shouldFindMessagesByUsername() {
        // given
        String username = "username";
        int page = 0;
        int size = 1;
        String sortField = "id";
        Direction direction = Direction.ASC;

        when(messageRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(mock(Page.class));

        // when
        underTest.findMessagesByUsername(username, page, size, sortField, direction);

        // then
        ArgumentCaptor<Specification> specificationArgumentCaptor =
                ArgumentCaptor.forClass(Specification.class);

        ArgumentCaptor<PageRequest> pageRequestArgumentCaptor =
                ArgumentCaptor.forClass(PageRequest.class);


        verify(messageRepository).findAll(specificationArgumentCaptor.capture(),
                pageRequestArgumentCaptor.capture());

        Specification capturedSpecification = specificationArgumentCaptor.getValue();
        PageRequest capturedPageRequest = pageRequestArgumentCaptor.getValue();

        assertThat(capturedSpecification).isInstanceOf(Specification.class);
        assertThat(capturedPageRequest).isInstanceOf(PageRequest.class);
    }

    @Test
    void shouldThrowBlankMessageExceptionWhenMessageDtoIsNullOrTextIsBlank() {
        // given
        MessageDto messageDto = MessageDto.builder().text(" ").build();

        // when
        // then
        String messageIsBlank = "Message is blank";
        assertThatThrownBy(() -> underTest.saveMessage(null))
                .isInstanceOf(BlankMessageException.class)
                .hasMessageContaining(messageIsBlank);

        assertThatThrownBy(() -> underTest.saveMessage(messageDto))
                .isInstanceOf(BlankMessageException.class)
                .hasMessageContaining(messageIsBlank);

    }

    @Test
    void shouldThrowUserNotFoundExceptionUserDoesNotExist() {
        // given
        String username = "notExistUsername";
        MessageDto messageDto = MessageDto.builder()
                .text("text")
                .username(username)
                .build();

        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.saveMessage(messageDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User: " + messageDto.getUsername() + " not found.");
    }

    @Test
    void shouldSaveMessageAndReturnMessageDto() {
        // given
        String text = "text";
        LocalDateTime createdTime = LocalDateTime.now();

        User user = mock(User.class);

        MessageDto messageDto = MessageDto.builder()
                .text(text)
                .createdTime(createdTime)
                .username(user.getUsername())
                .build();

        Message message = new Message();
        message.setText(text);
        message.setCreatedTime(createdTime);
        message.setUser(user);

        // for mapping to dto
        Message savedMessage = new Message();
        savedMessage.setId(1L);
        savedMessage.setText(text);
        savedMessage.setCreatedTime(createdTime);
        savedMessage.setUser(user);


        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));
        given(messageRepository.save(message)).willReturn(savedMessage);


        // when
        underTest.saveMessage(messageDto);

        // then
        ArgumentCaptor<Message> messageArgumentCaptor =
                ArgumentCaptor.forClass(Message.class);

        verify(messageRepository).save(messageArgumentCaptor.capture());

        Message capturedMessage = messageArgumentCaptor.getValue();

        assertThat(capturedMessage).isEqualTo(message);
    }
}