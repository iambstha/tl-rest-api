package com.iambstha.tl_rest_api.repository.refreshToken;

import com.iambstha.tl_rest_api.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "spring.datasource.platform", havingValue = "postgres")
public interface PostgresRefreshTokenRepository extends JpaRepository<RefreshToken, Long>, RefreshTokenRepositoryCustom {

    @Query(value = "select (created_ts + (:expirationTime * interval '1 minute')) from tl_refresh_token " +
            "where refresh_token=:refreshToken and token=:token", nativeQuery = true)
    Optional<Timestamp> expirationTimestamp(Long expirationTime, String refreshToken, String token);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM tl_refresh_token " +
            "WHERE :currentTimestamp > (created_ts + (:expirationTime * interval '1 minute'))",
            nativeQuery = true)
    void deleteExpiredTokens(Timestamp currentTimestamp, Long expirationTime);

}
