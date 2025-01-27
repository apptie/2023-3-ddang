package com.ddang.ddang.notification.application.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddang.ddang.notification.application.NotificationProperty;
import java.util.List;
import org.junit.jupiter.api.Test;

class NotificationPropertyTest {

    @Test
    void 알림_메시지_key이름을_반환한다() {
        // given
        final NotificationProperty[] values = NotificationProperty.values();

        // when & then
        assertThat(values).containsAll(List.of(
                NotificationProperty.NOTIFICATION_TYPE,
                NotificationProperty.IMAGE,
                NotificationProperty.TITLE,
                NotificationProperty.BODY,
                NotificationProperty.REDIRECT_URL
        ));
    }
}
