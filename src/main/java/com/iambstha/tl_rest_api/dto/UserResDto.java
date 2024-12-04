package com.iambstha.tl_rest_api.dto;

import com.iambstha.tl_rest_api.entity.enums.general.Gender;
import com.iambstha.tl_rest_api.entity.enums.user.UserRole;
import com.iambstha.tl_rest_api.entity.enums.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserResDto {

    private Long userId;
    private String username;
    private String email;
    private Boolean isEmailVerified;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Date dob;
    private String phoneNumber;
    private Boolean isPhoneVerified;
    private Timestamp lastPasswordChangedDate;
    private Integer lastPasswordChangedBy;
    private Timestamp lastLogin;
    private Integer noOfFailedLogins;
    private Boolean isTwoFaEnabled;
    private UserStatus status;
    private String profileImage;
    private UserRole role;

}
