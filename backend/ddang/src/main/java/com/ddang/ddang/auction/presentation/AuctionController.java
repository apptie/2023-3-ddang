package com.ddang.ddang.auction.presentation;

import com.ddang.ddang.auction.application.AuctionService;
import com.ddang.ddang.auction.application.dto.request.CreateAuctionDto;
import com.ddang.ddang.auction.application.dto.response.CreateInfoAuctionDto;
import com.ddang.ddang.auction.application.dto.response.ReadSingleAuctionDto;
import com.ddang.ddang.auction.application.dto.response.ReadMultipleAuctionDto;
import com.ddang.ddang.chat.application.dto.response.ReadChatRoomDto;
import com.ddang.ddang.auction.configuration.DescendingSort;
import com.ddang.ddang.auction.presentation.dto.request.CreateAuctionRequest;
import com.ddang.ddang.auction.presentation.dto.request.ReadAuctionSearchCondition;
import com.ddang.ddang.auction.presentation.dto.response.CreateAuctionResponse;
import com.ddang.ddang.auction.presentation.dto.response.ReadSingleAuctionResponse;
import com.ddang.ddang.auction.presentation.dto.response.ReadMultipleAuctionResponse;
import com.ddang.ddang.authentication.configuration.AuthenticateUser;
import com.ddang.ddang.authentication.domain.dto.AuthenticationUserInfo;
import com.ddang.ddang.chat.application.ChatRoomService;
import com.ddang.ddang.image.presentation.util.ImageUrlFinder;
import com.ddang.ddang.image.presentation.util.ImageTargetType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;
    private final ChatRoomService chatRoomService;
    private final ImageUrlFinder urlFinder;

    @PostMapping
    public ResponseEntity<CreateAuctionResponse> create(
            @AuthenticateUser final AuthenticationUserInfo userInfo,
            @RequestPart final List<MultipartFile> images,
            @RequestPart @Valid final CreateAuctionRequest request
    ) {
        final CreateInfoAuctionDto createInfoAuctionDto = auctionService.create(CreateAuctionDto.of(
                request,
                images,
                userInfo.userId()
        ));
        final CreateAuctionResponse response = CreateAuctionResponse.of(createInfoAuctionDto,
                urlFinder.find(ImageTargetType.AUCTION_IMAGE)
        );

        return ResponseEntity.created(URI.create("/auctions/" + createInfoAuctionDto.id()))
                             .body(response);
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<ReadSingleAuctionResponse> read(
            @AuthenticateUser final AuthenticationUserInfo userInfo,
            @PathVariable final Long auctionId
    ) {
        final ReadSingleAuctionDto readSingleAuctionDto = auctionService.readByAuctionId(auctionId);
        final ReadChatRoomDto readChatRoomDto = chatRoomService.readChatInfoByAuctionId(auctionId, userInfo);
        final ReadSingleAuctionResponse response = ReadSingleAuctionResponse.of(
                readSingleAuctionDto,
                userInfo,
                readChatRoomDto,
                urlFinder.find(ImageTargetType.PROFILE_IMAGE),
                urlFinder.find(ImageTargetType.AUCTION_IMAGE)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ReadMultipleAuctionResponse> readAllByCondition(
            @AuthenticateUser final AuthenticationUserInfo ignored,
            @DescendingSort final Pageable pageable,
            final ReadAuctionSearchCondition readAuctionSearchCondition
    ) {
        final ReadMultipleAuctionDto readMultipleAuctionDto = auctionService.readAllByCondition(
                pageable,
                readAuctionSearchCondition
        );
        final ReadMultipleAuctionResponse response = ReadMultipleAuctionResponse.of(
                readMultipleAuctionDto,
                urlFinder.find(ImageTargetType.AUCTION_IMAGE)
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{auctionId}")
    public ResponseEntity<Void> delete(
            @AuthenticateUser final AuthenticationUserInfo userInfo,
            @PathVariable final Long auctionId
    ) {
        auctionService.deleteByAuctionId(auctionId, userInfo.userId());

        return ResponseEntity.noContent().build();
    }
}
