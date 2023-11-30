package com.ddang.ddang.device.application;

import com.ddang.ddang.device.application.dto.request.CreateDeviceTokenDto;
import com.ddang.ddang.device.domain.DeviceToken;
import com.ddang.ddang.device.domain.repository.DeviceTokenRepository;
import com.ddang.ddang.user.domain.User;
import com.ddang.ddang.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void persist(final Long userId, final CreateDeviceTokenDto deviceTokenDto) {
        deviceTokenDto.findDeviceToken()
                      .ifPresent(
                              deviceToken -> processDeviceToken(userId, deviceToken));
    }

    private void processDeviceToken(final Long userId, final String targetDeviceToken) {
        deviceTokenRepository.findByUserId(userId)
                             .ifPresentOrElse(
                                     persistDeviceToken -> updateDeviceToken(persistDeviceToken, targetDeviceToken),
                                     () -> persistDeviceToken(userId, targetDeviceToken)
                             );
    }

    private void updateDeviceToken(final DeviceToken deviceToken, final String newDeviceTokenValue) {
        if (deviceToken.isDifferentToken(newDeviceTokenValue)) {
            deviceToken.updateDeviceToken(newDeviceTokenValue);
        }
    }

    private void persistDeviceToken(final Long userId, final String newDeviceToken) {
        final User findUser = userRepository.getByIdOrThrow(userId);
        final DeviceToken deviceToken = new DeviceToken(findUser, newDeviceToken);

        deviceTokenRepository.save(deviceToken);
    }
}
