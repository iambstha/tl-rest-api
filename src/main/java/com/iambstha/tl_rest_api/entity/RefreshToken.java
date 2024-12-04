package com.iambstha.tl_rest_api.entity;

import com.iambstha.tl_rest_api.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "tl_refresh_token")
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID refreshTokenId;

    @Column(name = "token", columnDefinition = "text")
    private String token;

    @Column(name = "refresh_token", columnDefinition = "text")
    private String refreshToken;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "host_address")
    private String hostAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "user_id")
    private Long userId;

}
