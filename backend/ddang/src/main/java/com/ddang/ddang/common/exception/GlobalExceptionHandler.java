package com.ddang.ddang.common.exception;

import com.ddang.ddang.auction.application.exception.UserForbiddenException;
import com.ddang.ddang.auction.configuration.exception.InvalidSearchConditionException;
import com.ddang.ddang.auction.domain.exception.InvalidPriceValueException;
import com.ddang.ddang.auction.domain.exception.WinnerNotFoundException;
import com.ddang.ddang.auction.infrastructure.persistence.exception.AuctionNotFoundException;
import com.ddang.ddang.authentication.application.exception.InvalidWithdrawalException;
import com.ddang.ddang.authentication.application.exception.WithdrawalNotAllowedException;
import com.ddang.ddang.authentication.configuration.exception.UserUnauthorizedException;
import com.ddang.ddang.authentication.domain.exception.InvalidTokenException;
import com.ddang.ddang.authentication.domain.exception.UnsupportedSocialLoginException;
import com.ddang.ddang.bid.application.exception.InvalidAuctionToBidException;
import com.ddang.ddang.bid.application.exception.InvalidBidPriceException;
import com.ddang.ddang.bid.application.exception.InvalidBidderException;
import com.ddang.ddang.category.infrastructure.exception.CategoryNotFoundException;
import com.ddang.ddang.chat.application.exception.ForbiddenChattingUserException;
import com.ddang.ddang.chat.application.exception.InvalidAuctionToChatException;
import com.ddang.ddang.chat.application.exception.MessageNotFoundException;
import com.ddang.ddang.chat.application.exception.UnableToChatException;
import com.ddang.ddang.chat.infrastructure.exception.ChatRoomNotFoundException;
import com.ddang.ddang.chat.infrastructure.exception.ReadMessageLogNotFoundException;
import com.ddang.ddang.common.exception.dto.ExceptionResponse;
import com.ddang.ddang.device.application.exception.DeviceTokenNotFoundException;
import com.ddang.ddang.image.application.exception.ImageNotFoundException;
import com.ddang.ddang.image.infrastructure.local.exception.EmptyImageException;
import com.ddang.ddang.image.infrastructure.local.exception.StoreImageFailureException;
import com.ddang.ddang.image.infrastructure.local.exception.UnsupportedImageFileExtensionException;
import com.ddang.ddang.image.infrastructure.persistence.exception.AuctionImageNotFoundException;
import com.ddang.ddang.notification.application.exception.NotificationFailedException;
import com.ddang.ddang.qna.application.exception.AlreadyAnsweredException;
import com.ddang.ddang.qna.application.exception.InvalidAnswererException;
import com.ddang.ddang.qna.application.exception.InvalidAuctionToAskQuestionException;
import com.ddang.ddang.qna.application.exception.InvalidQuestionerException;
import com.ddang.ddang.qna.infrastructure.exception.AnswerNotFoundException;
import com.ddang.ddang.qna.infrastructure.exception.QuestionNotFoundException;
import com.ddang.ddang.region.application.exception.RegionNotFoundException;
import com.ddang.ddang.report.application.exception.AlreadyReportAuctionException;
import com.ddang.ddang.report.application.exception.AlreadyReportChatRoomException;
import com.ddang.ddang.report.application.exception.InvalidChatRoomReportException;
import com.ddang.ddang.report.application.exception.InvalidQuestionReportException;
import com.ddang.ddang.report.application.exception.InvalidReportAuctionException;
import com.ddang.ddang.report.application.exception.InvalidReporterToAuctionException;
import com.ddang.ddang.review.application.exception.AlreadyReviewException;
import com.ddang.ddang.review.infrastructure.exception.ReviewNotFoundException;
import com.ddang.ddang.user.application.exception.AlreadyExistsNameException;
import com.ddang.ddang.user.infrastructure.exception.UserNotFoundException;
import java.net.MalformedURLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String LOG_MESSAGE_FORMAT = "%s : %s";

    @ExceptionHandler({
            InvalidAuctionToBidException.class,
            InvalidBidderException.class,
            InvalidBidPriceException.class,
            InvalidPriceValueException.class,
            EmptyImageException.class,
            UnsupportedImageFileExtensionException.class,
            InvalidAuctionToChatException.class,
            UnsupportedSocialLoginException.class,
            AlreadyReviewException.class,
            UnableToChatException.class,
            WithdrawalNotAllowedException.class,
            AlreadyExistsNameException.class,
            AlreadyAnsweredException.class,
            InvalidQuestionReportException.class,
            InvalidAnswererException.class,
            InvalidAuctionToAskQuestionException.class,
            InvalidQuestionerException.class,
            InvalidSearchConditionException.class,
            AlreadyReportChatRoomException.class,
            InvalidReporterToAuctionException.class,
            AlreadyReportAuctionException.class,
            InvalidReportAuctionException.class
    })
    public ResponseEntity<ExceptionResponse> handleBadRequestException(final Exception ex) {
        logger.warn(String.format(LOG_MESSAGE_FORMAT, ex.getClass().getSimpleName(), ex.getMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new ExceptionResponse(ex.getMessage()));
    }

    @ExceptionHandler({
            InvalidTokenException.class,
            UserUnauthorizedException.class
    })
    public ResponseEntity<ExceptionResponse> handleUnAuthorizedException(final Exception ex) {
        logger.warn(String.format(LOG_MESSAGE_FORMAT, ex.getClass().getSimpleName(), ex.getMessage()));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(new ExceptionResponse(ex.getMessage()));
    }

    @ExceptionHandler({
            UserForbiddenException.class,
            InvalidWithdrawalException.class,
            ForbiddenChattingUserException.class,
            InvalidChatRoomReportException.class,
    })
    public ResponseEntity<ExceptionResponse> handleForbiddenException(final Exception ex) {
        logger.warn(String.format(LOG_MESSAGE_FORMAT, ex.getClass().getSimpleName(), ex.getMessage()));

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(new ExceptionResponse(ex.getMessage()));
    }

    @ExceptionHandler({
            CategoryNotFoundException.class,
            RegionNotFoundException.class,
            ChatRoomNotFoundException.class,
            MessageNotFoundException.class,
            ReadMessageLogNotFoundException.class,
            UserNotFoundException.class,
            AuctionNotFoundException.class,
            ImageNotFoundException.class,
            WinnerNotFoundException.class,
            AnswerNotFoundException.class,
            DeviceTokenNotFoundException.class,
            ReviewNotFoundException.class,
            QuestionNotFoundException.class
    })
    public ResponseEntity<ExceptionResponse> handleNotFoundException(final Exception ex) {
        logger.warn(String.format(LOG_MESSAGE_FORMAT, ex.getClass().getSimpleName(), ex.getMessage()));

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body(new ExceptionResponse(ex.getMessage()));
    }

    @ExceptionHandler({
            NotificationFailedException.class,
            StoreImageFailureException.class,
            AuctionImageNotFoundException.class
    })
    public ResponseEntity<ExceptionResponse> handleInternalServerErrorException(final Exception ex) {
        logger.warn(String.format(LOG_MESSAGE_FORMAT, ex.getClass().getSimpleName(), ex.getMessage()));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ExceptionResponse(ex.getMessage()));
    }

    @ExceptionHandler(MalformedURLException.class)
    public ResponseEntity<ExceptionResponse> handleMalformedURLException(final MalformedURLException ex) {
        logger.error(String.format(LOG_MESSAGE_FORMAT, ex.getClass().getSimpleName(), ex.getMessage()), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ExceptionResponse("이미지 조회에 실패했습니다."));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex,
            final HttpHeaders ignoredHeaders,
            final HttpStatusCode ignoredStatus,
            final WebRequest ignoredRequest
    ) {
        logger.warn(String.format(LOG_MESSAGE_FORMAT, ex.getClass().getSimpleName(), ex.getMessage()));

        final String message = ex.getFieldErrors()
                                 .get(0)
                                 .getDefaultMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(new ExceptionResponse(message));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            final Exception ex,
            final Object body,
            final HttpHeaders headers,
            final HttpStatusCode statusCode,
            final WebRequest request
    ) {
        logger.error(String.format(LOG_MESSAGE_FORMAT, ex.getClass().getSimpleName(), ex.getMessage()), ex);

        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }
}
