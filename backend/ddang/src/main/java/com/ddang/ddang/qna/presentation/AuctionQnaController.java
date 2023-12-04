package com.ddang.ddang.qna.presentation;

import com.ddang.ddang.qna.presentation.dto.response.ReadMultipleQnaResponse;
import com.ddang.ddang.authentication.configuration.AuthenticateUser;
import com.ddang.ddang.authentication.domain.dto.AuthenticationUserInfo;
import com.ddang.ddang.image.presentation.util.ImageUrlFinder;
import com.ddang.ddang.image.presentation.util.ImageTargetType;
import com.ddang.ddang.qna.application.QuestionService;
import com.ddang.ddang.qna.application.dto.response.ReadMultipleQnaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class AuctionQnaController {

    private final QuestionService questionService;
    private final ImageUrlFinder urlFinder;

    @GetMapping("/{auctionId}/questions")
    public ResponseEntity<ReadMultipleQnaResponse> readAllByAuctionId(
            @AuthenticateUser AuthenticationUserInfo userInfo,
            @PathVariable final Long auctionId
    ) {
        final ReadMultipleQnaDto readMultipleQnaDto = questionService.readAllByAuctionId(auctionId, userInfo.userId());
        final ReadMultipleQnaResponse response = ReadMultipleQnaResponse.of(
                readMultipleQnaDto,
                urlFinder.find(ImageTargetType.PROFILE_IMAGE)
        );

        return ResponseEntity.ok(response);
    }
}
