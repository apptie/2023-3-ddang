package com.ddang.ddang.chat.application;

import com.ddang.ddang.chat.application.dto.CreateMessageDto;
import com.ddang.ddang.chat.application.exception.ChatRoomNotFoundException;
import com.ddang.ddang.chat.application.exception.UserNotFoundException;
import com.ddang.ddang.chat.domain.ChatRoom;
import com.ddang.ddang.chat.domain.Message;
import com.ddang.ddang.chat.infrastructure.persistence.JpaChatRoomRepository;
import com.ddang.ddang.chat.infrastructure.persistence.JpaMessageRepository;
import com.ddang.ddang.user.domain.User;
import com.ddang.ddang.user.infrastructure.persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {

    private final JpaMessageRepository messageRepository;
    private final JpaChatRoomRepository chatRoomRepository;
    private final JpaUserRepository userRepository;

    @Transactional
    public Long create(final CreateMessageDto dto) {
        final ChatRoom chatRoom = getChatRoom(dto);
        final User writer = getUser(dto.writerId(), "지정한 아이디에 대한 발신자를 찾을 수 없습니다.");
        final User receiver = getUser(dto.receiverId(), "지정한 아이디에 대한 수신자를 찾을 수 없습니다.");
        final Message message = dto.toEntity(chatRoom, writer, receiver);

        return messageRepository.save(message)
                                .getId();
    }

    private ChatRoom getChatRoom(final CreateMessageDto dto) {
        return chatRoomRepository.findById(dto.chatRoomId())
                                 .orElseThrow(() -> new ChatRoomNotFoundException(
                                         "지정한 아이디에 대한 채팅방을 찾을 수 없습니다."
                                 ));
    }

    private User getUser(final Long dto, final String message) {
        return userRepository.findById(dto)
                             .orElseThrow(() -> new UserNotFoundException(message));
    }
}