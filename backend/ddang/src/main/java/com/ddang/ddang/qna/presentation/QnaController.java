package com.ddang.ddang.qna.presentation;

import com.ddang.ddang.authentication.configuration.AuthenticateUser;
import com.ddang.ddang.authentication.domain.dto.AuthenticationUserInfo;
import com.ddang.ddang.image.presentation.util.ImageUrlFinder;
import com.ddang.ddang.image.presentation.util.ImageTargetType;
import com.ddang.ddang.qna.application.AnswerService;
import com.ddang.ddang.qna.application.QuestionService;
import com.ddang.ddang.qna.application.dto.request.CreateAnswerDto;
import com.ddang.ddang.qna.application.dto.request.CreateQuestionDto;
import com.ddang.ddang.qna.presentation.dto.request.CreateAnswerRequest;
import com.ddang.ddang.qna.presentation.dto.request.CreateQuestionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QnaController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final ImageUrlFinder urlFinder;

    @PostMapping
    public ResponseEntity<Void> createQuestion(
            @AuthenticateUser AuthenticationUserInfo userInfo,
            @RequestBody @Valid final CreateQuestionRequest questionRequest
    ) {
        questionService.create(
                CreateQuestionDto.of(questionRequest, userInfo.userId()),
                urlFinder.find(ImageTargetType.AUCTION_IMAGE)
        );

        return ResponseEntity.created(URI.create("/auctions/" + questionRequest.auctionId()))
                             .build();
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @AuthenticateUser AuthenticationUserInfo userInfo,
            @PathVariable final Long questionId
    ) {
        questionService.deleteById(questionId, userInfo.userId());

        return ResponseEntity.noContent()
                             .build();
    }

    @PostMapping("/{questionId}/answers")
    public ResponseEntity<Void> createAnswer(
            @AuthenticateUser AuthenticationUserInfo userInfo,
            @PathVariable final Long questionId,
            @RequestBody @Valid final CreateAnswerRequest answerRequest
    ) {
        answerService.create(
                CreateAnswerDto.of(questionId, answerRequest, userInfo.userId()),
                urlFinder.find(ImageTargetType.AUCTION_IMAGE)
        );

        return ResponseEntity.created(URI.create("/auctions/" + answerRequest.auctionId()))
                             .build();
    }

    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(
            @AuthenticateUser AuthenticationUserInfo userInfo,
            @PathVariable final Long answerId
    ) {
        answerService.deleteById(answerId, userInfo.userId());

        return ResponseEntity.noContent()
                             .build();
    }
}
