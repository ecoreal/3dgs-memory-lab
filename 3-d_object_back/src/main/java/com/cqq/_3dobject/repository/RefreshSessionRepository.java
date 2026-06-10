package com.cqq._3dobject.repository;

import com.cqq._3dobject.entity.RefreshSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshSessionRepository extends JpaRepository<RefreshSession, Long> {

    Optional<RefreshSession> findByRefreshTokenHash(String refreshTokenHash);

    @Modifying
    @Query("UPDATE RefreshSession r SET r.status = 'revoked' WHERE r.sessionId = :sessionId AND r.status = 'active'")
    int revokeBySessionId(@Param("sessionId") String sessionId);

    @Modifying
    @Query("UPDATE RefreshSession r SET r.status = 'revoked' WHERE r.uid = :uid AND r.status = 'active'")
    int revokeAllByUid(@Param("uid") Long uid);
}
