package com.iambstha.tl_rest_api.repository.refreshToken;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenRepositoryCustom {

    Boolean existsRefreshTokenByRefreshToken(String refreshToken);

    @Modifying
    @Query(value = "DELETE FROM tl_refresh_token WHERE refresh_token = :refreshToken", nativeQuery = true)
    void deleteByRefreshToken(String refreshToken);

    @Modifying
    @Query(value = "delete from tl_refresh_token where user_id=:userId and refresh_token=:refreshToken and token=:token and ip_address=:ipAddress and coalesce(host_address,'')=:hostAddress and user_agent=:userAgent", nativeQuery = true)
    Integer deleteUserToken(Long userId, String token, String refreshToken, String ipAddress, String hostAddress, String userAgent);

}

