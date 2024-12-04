package com.iambstha.tl_rest_api.dto;

import com.iambstha.tl_rest_api.entity.enums.general.Gender;
import com.iambstha.tl_rest_api.entity.enums.user.UserRole;
import com.iambstha.tl_rest_api.entity.enums.user.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class UserReqDto {

    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Date dob;
    private String phoneNumber;
    private Boolean isTwoFaEnabled;
    private UserStatus status;
    private UserRole role;

}
