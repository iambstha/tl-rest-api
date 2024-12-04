package com.iambstha.tl_rest_api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PasswordChangeDto {

    @NotEmpty(message = "User name is required")
        private String userName;

    private String oldPassword;

    @NotEmpty(message = "Password is required")
    private String newPassword;

    private String newPasswordAgain;

}
