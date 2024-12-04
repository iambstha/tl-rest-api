package com.iambstha.tl_rest_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginTokenDto {

    private Long userId;
    private String username;
    private String token;
    private String refreshToken;
    private String firstName;
    private String lastName;

}
