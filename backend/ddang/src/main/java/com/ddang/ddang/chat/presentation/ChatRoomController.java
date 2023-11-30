package com.ddang.ddang.chat.presentation;

import com.ddang.ddang.authentication.configuration.AuthenticateUser;
import com.ddang.ddang.authentication.domain.dto.AuthenticationUserInfo;
import com.ddang.ddang.chat.application.ChatRoomService;
import com.ddang.ddang.chat.application.MessageService;
import com.ddang.ddang.chat.application.dto.request.CreateChatRoomDto;
import com.ddang.ddang.chat.application.dto.request.CreateMessageDto;
import com.ddang.ddang.chat.application.dto.response.ReadMultipleChatRoomDto;
import com.ddang.ddang.chat.application.dto.response.ReadMultipleMessageDto;
import com.ddang.ddang.chat.application.dto.response.ReadSingleChatRoomDto;
import com.ddang.ddang.chat.presentation.dto.request.CreateChatRoomRequest;
import com.ddang.ddang.chat.presentation.dto.request.CreateMessageRequest;
import com.ddang.ddang.chat.application.dto.request.ReadMessageDto;
import com.ddang.ddang.chat.presentation.dto.response.CreateChatRoomResponse;
import com.ddang.ddang.chat.presentation.dto.response.CreateMessageResponse;
import com.ddang.ddang.chat.presentation.dto.response.ReadSingleChatRoomResponse;
import com.ddang.ddang.chat.presentation.dto.response.ReadMultipleChatRoomResponse;
import com.ddang.ddang.chat.presentation.dto.response.ReadMessageResponse;
import com.ddang.ddang.image.presentation.util.ImageRelativeUrlFinder;
import com.ddang.ddang.image.presentation.util.ImageTargetType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/chattings")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;
    private final ImageRelativeUrlFinder urlFinder;

    @PostMapping
    public ResponseEntity<CreateChatRoomResponse> createChatRoom(
            @AuthenticateUser final AuthenticationUserInfo userInfo,
            @RequestBody @Valid final CreateChatRoomRequest chatRoomRequest
    ) {
        final Long chatRoomId = chatRoomService.create(userInfo.userId(), CreateChatRoomDto.from(chatRoomRequest));
        final CreateChatRoomResponse response = new CreateChatRoomResponse(chatRoomId);

        return ResponseEntity.created(URI.create("/chattings/" + chatRoomId))
                             .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ReadMultipleChatRoomResponse>> readAllParticipatingChatRooms(
            @AuthenticateUser final AuthenticationUserInfo userInfo
    ) {
        final List<ReadMultipleChatRoomDto> readMultipleChatRoomDtos = chatRoomService.readAllByUserId(userInfo.userId());
        final List<ReadMultipleChatRoomResponse> responses =
                readMultipleChatRoomDtos.stream()
                                        .map(dto -> ReadMultipleChatRoomResponse.of(
                                                     dto,
                                                     urlFinder.find(ImageTargetType.PROFILE_IMAGE),
                                                     urlFinder.find(ImageTargetType.AUCTION_IMAGE)
                                             ))
                                        .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<ReadSingleChatRoomResponse> readChatRoomById(
            @AuthenticateUser final AuthenticationUserInfo userInfo,
            @PathVariable final Long chatRoomId
    ) {
        final ReadSingleChatRoomDto chatRoomDto = chatRoomService.readByChatRoomId(
                chatRoomId,
                userInfo.userId()
        );
        final ReadSingleChatRoomResponse response = ReadSingleChatRoomResponse.of(
                chatRoomDto,
                urlFinder.find(ImageTargetType.PROFILE_IMAGE),
                urlFinder.find(ImageTargetType.AUCTION_IMAGE)
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{chatRoomId}/messages")
    public ResponseEntity<CreateMessageResponse> createMessage(
            @AuthenticateUser final AuthenticationUserInfo userInfo,
            @PathVariable final Long chatRoomId,
            @RequestBody @Valid final CreateMessageRequest request
    ) {

        final Long messageId = messageService.create(
                CreateMessageDto.of(userInfo.userId(), chatRoomId, request),
                urlFinder.find(ImageTargetType.PROFILE_IMAGE)
        );
        final CreateMessageResponse response = new CreateMessageResponse(messageId);

        return ResponseEntity.created(URI.create("/chattings/" + chatRoomId))
                             .body(response);
    }

    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<List<ReadMessageResponse>> readAllByLastMessageId(
            @AuthenticateUser final AuthenticationUserInfo userInfo,
            @PathVariable final Long chatRoomId,
            @RequestParam(required = false) final Long lastMessageId
    ) {
        final ReadMessageDto readMessageDto = new ReadMessageDto(
                userInfo.userId(),
                chatRoomId,
                lastMessageId
        );
        final List<ReadMultipleMessageDto> readMultipleMessageDtos = messageService.readAllByLastMessageId(
                readMessageDto);
        final List<ReadMessageResponse> responses = readMultipleMessageDtos.stream()
                                                                           .map(dto -> ReadMessageResponse.of(
                                                                           dto,
                                                                           isMessageOwner(
                                                                                   dto,
                                                                                   userInfo
                                                                           )
                                                                   ))
                                                                           .toList();
        return ResponseEntity.ok(responses);
    }

    private boolean isMessageOwner(final ReadMultipleMessageDto readMultipleMessageDto, final AuthenticationUserInfo userInfo) {
        return readMultipleMessageDto.writerId()
                                     .equals(userInfo.userId());
    }
}
