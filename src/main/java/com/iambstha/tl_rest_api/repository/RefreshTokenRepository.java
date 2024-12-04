package com.iambstha.tl_rest_api.repository;

import com.iambstha.tl_rest_api.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Boolean existsRefreshTokenByRefreshToken(String refreshToken);

    @Query(value = "select (created_ts + (:expirationTime * interval '1 minute')) from tl_refresh_token " +
            "where refresh_token=:refreshToken and token=:token", nativeQuery = true)
    Optional<Timestamp> expirationTimestamp(Long expirationTime, String refreshToken, String token);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM tl_refresh_token " +
            "WHERE :currentTimestamp > (created_ts + (:expirationTime * interval '1 minute'))",
            nativeQuery = true)
    void deleteExpiredTokens(Timestamp currentTimestamp, Long expirationTime);


    @Modifying
    @Query(value = "DELETE FROM tl_refresh_token WHERE refresh_token = :refreshToken", nativeQuery = true)
    void deleteByRefreshToken(String refreshToken);

    @Modifying
    @Query(value = "delete from tl_refresh_token where user_id=:userId and refresh_token=:refreshToken and token=:token and ip_address=:ipAddress and coalesce(host_address,'')=:hostAddress and user_agent=:userAgent", nativeQuery = true)
    Integer deleteUserToken(Long userId, String token, String refreshToken, String ipAddress, String hostAddress, String userAgent);


}
