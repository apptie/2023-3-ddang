package com.ddang.ddang.chat.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddang.ddang.auction.domain.Auction;
import com.ddang.ddang.auction.domain.BidUnit;
import com.ddang.ddang.auction.domain.Price;
import com.ddang.ddang.auction.infrastructure.persistence.JpaAuctionRepository;
import com.ddang.ddang.category.domain.Category;
import com.ddang.ddang.category.infrastructure.persistence.JpaCategoryRepository;
import com.ddang.ddang.chat.application.dto.CreateMessageDto;
import com.ddang.ddang.chat.application.exception.ChatRoomNotFoundException;
import com.ddang.ddang.chat.application.exception.UserNotFoundException;
import com.ddang.ddang.chat.domain.ChatRoom;
import com.ddang.ddang.chat.infrastructure.persistence.JpaChatRoomRepository;
import com.ddang.ddang.chat.infrastructure.persistence.JpaMessageRepository;
import com.ddang.ddang.configuration.IsolateDatabase;
import com.ddang.ddang.user.domain.User;
import com.ddang.ddang.user.infrastructure.persistence.JpaUserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IsolateDatabase
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class MessageServiceTest {

    @Autowired
    MessageService messageService;

    @Autowired
    JpaMessageRepository messageRepository;

    @Autowired
    JpaAuctionRepository auctionRepository;

    @Autowired
    JpaUserRepository userRepository;

    @Autowired
    JpaChatRoomRepository chatRoomRepository;

    @Autowired
    JpaCategoryRepository categoryRepository;

    @Test
    void 메시지를_생성한다() {
        // given
        final BidUnit bidUnit = new BidUnit(1_000);
        final Price startPrice = new Price(10_000);
        final Category main = new Category("전자기기");
        final Category sub = new Category("노트북");

        main.addSubCategory(sub);

        categoryRepository.save(main);
        final Auction auction = Auction.builder()
                                       .title("title")
                                       .description("description")
                                       .bidUnit(bidUnit)
                                       .startPrice(startPrice)
                                       .closingTime(LocalDateTime.now().plusDays(3L))
                                       .build();

        auctionRepository.save(auction);

        final User writer = User.builder()
                                .name("발신자")
                                .profileImage("profile.png")
                                .reliability(4.7d)
                                .oauthId("12345")
                                .build();

        userRepository.save(writer);

        final User receiver = User.builder()
                                  .name("수신자")
                                  .profileImage("profile.png")
                                  .reliability(4.7d)
                                  .oauthId("12346")
                                  .build();

        userRepository.save(receiver);

        final ChatRoom chatRoom = new ChatRoom(auction, writer);

        chatRoomRepository.save(chatRoom);

        final String contents = "메시지 내용";

        final CreateMessageDto createMessageDto = new CreateMessageDto(
                chatRoom.getId(),
                writer.getId(),
                receiver.getId(),
                contents
        );

        // when
        final Long messageId = messageService.create(createMessageDto);

        // then
        assertThat(messageId).isPositive();
    }

    @Test
    void 채팅방이_없는_경우_메시지를_생성하면_예외가_발생한다() {
        // given
        final BidUnit bidUnit = new BidUnit(1_000);
        final Price startPrice = new Price(10_000);
        final Category main = new Category("전자기기");
        final Category sub = new Category("노트북");

        main.addSubCategory(sub);

        categoryRepository.save(main);
        final Auction auction = Auction.builder()
                                       .title("title")
                                       .description("description")
                                       .bidUnit(bidUnit)
                                       .startPrice(startPrice)
                                       .closingTime(LocalDateTime.now().plusDays(3L))
                                       .build();

        auctionRepository.save(auction);

        final User writer = User.builder()
                                .name("발신자")
                                .profileImage("profile.png")
                                .reliability(4.7d)
                                .oauthId("12345")
                                .build();

        userRepository.save(writer);

        final User receiver = User.builder()
                                  .name("수신자")
                                  .profileImage("profile.png")
                                  .reliability(4.7d)
                                  .oauthId("12346")
                                  .build();

        userRepository.save(receiver);

        final Long invalidChatRoomId = -999L;
        final String contents = "메시지 내용";

        final CreateMessageDto createMessageDto = new CreateMessageDto(
                invalidChatRoomId,
                writer.getId(),
                receiver.getId(),
                contents
        );

        // when & then
        assertThatThrownBy(() -> messageService.create(createMessageDto))
                .isInstanceOf(ChatRoomNotFoundException.class)
                .hasMessageContaining("지정한 아이디에 대한 채팅방을 찾을 수 없습니다.");
    }

    @Test
    void 발신자가_없는_경우_메시지를_생성하면_예외가_발생한다() {
        // given
        final BidUnit bidUnit = new BidUnit(1_000);
        final Price startPrice = new Price(10_000);
        final Category main = new Category("전자기기");
        final Category sub = new Category("노트북");

        main.addSubCategory(sub);

        categoryRepository.save(main);

        final Auction auction = Auction.builder()
                                       .title("title")
                                       .description("description")
                                       .bidUnit(bidUnit)
                                       .startPrice(startPrice)
                                       .closingTime(LocalDateTime.now().plusDays(3L))
                                       .build();

        auctionRepository.save(auction);

        final User receiver = User.builder()
                                  .name("수신자")
                                  .profileImage("profile.png")
                                  .reliability(4.7d)
                                  .oauthId("12345")
                                  .build();

        userRepository.save(receiver);


        final ChatRoom chatRoom = new ChatRoom(auction, receiver);

        chatRoomRepository.save(chatRoom);

        final String contents = "메시지 내용";
        final Long invalidWriterId = -999L;

        final CreateMessageDto createMessageDto = new CreateMessageDto(
                chatRoom.getId(),
                invalidWriterId,
                receiver.getId(),
                contents
        );

        assertThatThrownBy(() -> messageService.create(createMessageDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("지정한 아이디에 대한 발신자를 찾을 수 없습니다.");
    }

    @Test
    void 수신자가_없는_경우_메시지를_생성하면_예외가_발생한다() {
        // given
        final BidUnit bidUnit = new BidUnit(1_000);
        final Price startPrice = new Price(10_000);
        final Category main = new Category("전자기기");
        final Category sub = new Category("노트북");

        main.addSubCategory(sub);

        categoryRepository.save(main);

        final Auction auction = Auction.builder()
                                       .title("title")
                                       .description("description")
                                       .bidUnit(bidUnit)
                                       .startPrice(startPrice)
                                       .closingTime(LocalDateTime.now().plusDays(3L))
                                       .build();

        auctionRepository.save(auction);

        final User writer = User.builder()
                                .name("발신자")
                                .profileImage("profile.png")
                                .reliability(4.7d)
                                .oauthId("12345")
                                .build();

        userRepository.save(writer);


        final ChatRoom chatRoom = new ChatRoom(auction, writer);

        chatRoomRepository.save(chatRoom);

        final Long invalidReceiverId = -999L;
        final String contents = "메시지 내용";

        final CreateMessageDto createMessageDto = new CreateMessageDto(
                chatRoom.getId(),
                writer.getId(),
                invalidReceiverId,
                contents
        );

        assertThatThrownBy(() -> messageService.create(createMessageDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("지정한 아이디에 대한 수신자를 찾을 수 없습니다.");
    }
}