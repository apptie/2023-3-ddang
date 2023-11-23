package com.ddang.ddang.qna.presentation;

import com.ddang.ddang.qna.presentation.dto.response.ReadQnasResponse;
import com.ddang.ddang.authentication.configuration.AuthenticateUser;
import com.ddang.ddang.authentication.domain.dto.AuthenticationUserInfo;
import com.ddang.ddang.image.presentation.util.ImageRelativeUrlFinder;
import com.ddang.ddang.image.presentation.util.ImageTargetType;
import com.ddang.ddang.qna.application.QuestionService;
import com.ddang.ddang.qna.application.dto.response.ReadQnasDto;
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
    private final ImageRelativeUrlFinder urlFinder;

    @GetMapping("/{auctionId}/questions")
    public ResponseEntity<ReadQnasResponse> readAllByAuctionId(
            @AuthenticateUser AuthenticationUserInfo userInfo,
            @PathVariable final Long auctionId
    ) {
        final ReadQnasDto readQnasDto = questionService.readAllByAuctionId(auctionId, userInfo.userId());
        final ReadQnasResponse response = ReadQnasResponse.of(
                readQnasDto,
                urlFinder.find(ImageTargetType.PROFILE_IMAGE)
        );

        return ResponseEntity.ok(response);
    }
}
