package com.ddang.ddang.image.presentation.util;

import com.ddang.ddang.image.configuration.ImageRelativeUrlConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class ImageUrlFinder {

    private final ImageRelativeUrlConfigurationProperties imageRelativeUrlInfo;

    public String find(final ImageTargetType imageTargetType) {
        final String imageBaseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                                                               .build()
                                                               .toUriString();

        if (ImageTargetType.AUCTION_IMAGE == imageTargetType) {
            return imageBaseUrl + imageRelativeUrlInfo.auctionImage();
        }

        return imageBaseUrl + imageRelativeUrlInfo.profileImage();
    }
}
