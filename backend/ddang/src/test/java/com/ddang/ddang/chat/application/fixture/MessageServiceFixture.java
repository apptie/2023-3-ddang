package com.ddang.ddang.chat.application.fixture;

import com.ddang.ddang.auction.domain.Auction;
import com.ddang.ddang.auction.domain.BidUnit;
import com.ddang.ddang.auction.domain.Price;
import com.ddang.ddang.auction.domain.repository.AuctionRepository;
import com.ddang.ddang.category.domain.Category;
import com.ddang.ddang.category.infrastructure.persistence.JpaCategoryRepository;
import com.ddang.ddang.chat.application.dto.request.CreateMessageDto;
import com.ddang.ddang.chat.domain.ChatRoom;
import com.ddang.ddang.chat.domain.Message;
import com.ddang.ddang.chat.domain.repository.ChatRoomRepository;
import com.ddang.ddang.chat.domain.repository.MessageRepository;
import com.ddang.ddang.chat.application.dto.request.ReadMessageDto;
import com.ddang.ddang.image.domain.ProfileImage;
import com.ddang.ddang.user.domain.Reliability;
import com.ddang.ddang.user.domain.User;
import com.ddang.ddang.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class MessageServiceFixture {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private JpaCategoryRepository categoryRepository;

    protected CreateMessageDto 메시지_생성_DTO;
    protected CreateMessageDto 유효하지_않은_채팅방의_메시지_생성_DTO;
    protected CreateMessageDto 유효하지_않은_발신자의_메시지_생성_DTO;
    protected CreateMessageDto 유효하지_않은_수신자의_메시지_생성_DTO;
    protected CreateMessageDto 수신자가_탈퇴한_경우_메시지_생성_DTO;
    protected ReadMessageDto 마지막_조회_메시지_아이디가_없는_메시지_조회용_request;
    protected ReadMessageDto 두_번째_메시지부터_모든_메시지_조회용_request;
    protected ReadMessageDto 조회할_메시지가_더이상_없는_메시지_조회용_request;
    protected ReadMessageDto 조회한_마지막_메시지가_5인_메시지_조회용_request;
    protected ReadMessageDto 유효하지_않은_사용자의_메시지_조회용_request;
    protected ReadMessageDto 유효하지_않은_채팅방의_메시지_조회용_request;
    protected ReadMessageDto 존재하지_않는_마지막_메시지_아이디의_메시지_조회용_request;
    protected User 발신자;
    protected ChatRoom 메시지가_5개인_채팅방;
    protected Message 메시지가_5개인_채팅방_메시지의_마지막_메시지;

    protected String 이미지_절대_경로 = "/imageUrl";
    protected int 메시지_총_개수 = 10;

    @BeforeEach
    void setUp() {
        final Category 전자기기 = new Category("전자기기");
        final Category 전자기기_하위_노트북 = new Category("노트북");
        전자기기.addSubCategory(전자기기_하위_노트북);
        categoryRepository.save(전자기기);

        final Auction 경매_1 = Auction.builder()
                                  .title("경매")
                                  .description("description")
                                  .bidUnit(new BidUnit(1_000))
                                  .startPrice(new Price(10_000))
                                  .closingTime(LocalDateTime.now().plusDays(3L))
                                  .build();
        final Auction 경매_2 = Auction.builder()
                                    .title("경매")
                                    .description("description")
                                    .bidUnit(new BidUnit(1_000))
                                    .startPrice(new Price(10_000))
                                    .closingTime(LocalDateTime.now().plusDays(3L))
                                    .build();
        final Auction 경매_3 = Auction.builder()
                                    .title("경매")
                                    .description("description")
                                    .bidUnit(new BidUnit(1_000))
                                    .startPrice(new Price(10_000))
                                    .closingTime(LocalDateTime.now().plusDays(3L))
                                    .build();
        auctionRepository.save(경매_1);
        auctionRepository.save(경매_2);
        auctionRepository.save(경매_3);

        발신자 = User.builder()
                  .name("발신자")
                  .profileImage(new ProfileImage("upload.png", "store.png"))
                  .reliability(new Reliability(4.7d))
                  .oauthId("12345")
                  .build();
        final User 수신자 = User.builder()
                             .name("수신자")
                             .profileImage(new ProfileImage("upload.png", "store.png"))
                             .reliability(new Reliability(4.7d))
                             .oauthId("12346")
                             .build();
        final User 탈퇴한_사용자 = User.builder()
                                 .name("탈퇴한 사용자")
                                 .profileImage(new ProfileImage("upload.png", "store.png"))
                                 .reliability(new Reliability(4.7d))
                                 .oauthId("12347")
                                 .build();
        탈퇴한_사용자.withdrawal("탈퇴한 사용자");
        userRepository.save(발신자);
        userRepository.save(수신자);
        userRepository.save(탈퇴한_사용자);

        final ChatRoom 채팅방 = new ChatRoom(경매_1, 발신자);
        final ChatRoom 탈퇴한_사용자와의_채팅방 = new ChatRoom(경매_2, 탈퇴한_사용자);
        메시지가_5개인_채팅방 = new ChatRoom(경매_3, 발신자);

        chatRoomRepository.save(채팅방);
        chatRoomRepository.save(탈퇴한_사용자와의_채팅방);
        chatRoomRepository.save(메시지가_5개인_채팅방);

        메시지_생성_DTO = new CreateMessageDto(
                채팅방.getId(),
                발신자.getId(),
                수신자.getId(),
                "메시지 내용"
        );
        유효하지_않은_채팅방의_메시지_생성_DTO = new CreateMessageDto(
                -999L,
                발신자.getId(),
                수신자.getId(),
                "메시지 내용"
        );
        유효하지_않은_발신자의_메시지_생성_DTO = new CreateMessageDto(
                채팅방.getId(),
                -999L,
                수신자.getId(),
                "메시지 내용"
        );
        유효하지_않은_수신자의_메시지_생성_DTO = new CreateMessageDto(
                채팅방.getId(),
                발신자.getId(),
                -999L,
                "메시지 내용"
        );
        수신자가_탈퇴한_경우_메시지_생성_DTO = new CreateMessageDto(
                탈퇴한_사용자와의_채팅방.getId(),
                발신자.getId(),
                탈퇴한_사용자.getId(),
                "메시지 내용"
        );

        final List<Message> 메시지들 = new ArrayList<>();
        for (int count = 0; count < 메시지_총_개수; count++) {
            final Message 메시지 = Message.builder()
                                       .writer(발신자)
                                       .receiver(수신자)
                                       .chatRoom(채팅방)
                                       .content("메시지 내용")
                                       .build();
            메시지들.add(메시지);
            messageRepository.save(메시지);
        }

        final List<Message> 메시지가_5개인_채팅방_메시지들 = new ArrayList<>();
        for (int count = 0; count < 5; count++) {
            final Message 메시지 = Message.builder()
                                       .writer(발신자)
                                       .receiver(수신자)
                                       .chatRoom(메시지가_5개인_채팅방)
                                       .content("메시지 내용")
                                       .build();
            메시지가_5개인_채팅방_메시지들.add(메시지);
            messageRepository.save(메시지);
        }
        메시지가_5개인_채팅방_메시지의_마지막_메시지 = 메시지가_5개인_채팅방_메시지들.get(4);

        마지막_조회_메시지_아이디가_없는_메시지_조회용_request = new ReadMessageDto(발신자.getId(), 채팅방.getId(), null);
        두_번째_메시지부터_모든_메시지_조회용_request = new ReadMessageDto(발신자.getId(), 채팅방.getId(), 메시지들.get(0).getId());
        조회할_메시지가_더이상_없는_메시지_조회용_request = new ReadMessageDto(발신자.getId(), 채팅방.getId(), 메시지들.get(메시지_총_개수 - 1)
                                                                                           .getId());
        유효하지_않은_사용자의_메시지_조회용_request = new ReadMessageDto(-999L, 채팅방.getId(), null);
        유효하지_않은_채팅방의_메시지_조회용_request = new ReadMessageDto(발신자.getId(), -999L, null);
        존재하지_않는_마지막_메시지_아이디의_메시지_조회용_request = new ReadMessageDto(발신자.getId(), 채팅방.getId(), -999L);
        조회한_마지막_메시지가_5인_메시지_조회용_request = new ReadMessageDto(발신자.getId(), 메시지가_5개인_채팅방.getId(), null);
    }
}
