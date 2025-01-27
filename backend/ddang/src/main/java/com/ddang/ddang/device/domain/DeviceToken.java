package com.ddang.ddang.device.domain;

import com.ddang.ddang.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "deviceToken"})
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_device_token_user"))
    private User user;

    @Column(name = "device_token", unique = true, nullable = false)
    private String deviceToken;

    public DeviceToken(final User user, final String deviceToken) {
        this.user = user;
        this.deviceToken = deviceToken;
    }

    public boolean isDifferentToken(final String targetDeviceTokenValue) {
        return !this.deviceToken.equals(targetDeviceTokenValue);
    }

    public void updateDeviceToken(final String newDeviceTokenValue) {
        this.deviceToken = newDeviceTokenValue;
    }
}
