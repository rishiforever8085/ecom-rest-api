package com.ecommerce.user.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    UserEntity findByEmailAndActive(String email, Boolean active);

    @Modifying
    @Query("update UserEntity ue set ue.fcmToken = :fcmToken, ue.deviceType = :deviceType where ue.email = :email")
    int updateFcmTokenAndDeviceType(String fcmToken, String deviceType, String email);
}

