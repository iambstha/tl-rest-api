package com.iambstha.tl_rest_api.resource;

import com.iambstha.tl_rest_api.constant.AppConstant;
import com.iambstha.tl_rest_api.constant.StatusConstants;
import com.iambstha.tl_rest_api.domain.ApiResponse;
import com.iambstha.tl_rest_api.dto.*;
import com.iambstha.tl_rest_api.service.UserService;
import com.iambstha.tl_rest_api.validator.user.ValidOldPassword;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Tag(name = "User Management", description = "APIs related to managing users, including fetching, updating, and deleting user data.")
@RestController
@RequestMapping("v1/api/user")
@RequiredArgsConstructor
public class UserResource {

    private final static Logger logger = LoggerFactory.getLogger(UserResource.class);

    @Autowired
    @Qualifier("user")
    private MessageSource messageSource;

    private final Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    private final UserService userService;

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Get users", description = "Fetch a paginated list of all users.")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> getAllUsers(
            @RequestParam(name = "pageNum", defaultValue = "0", required = false) int pageNum,
            @RequestParam(name = "pageSize", defaultValue = "5", required = false) int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        List<String> details = new ArrayList<>();
        ApiResponse response = new ApiResponse(StatusConstants.FAILED, 400, AppConstant.DEFAULT_ERROR, null, null, null);

        Page<UserResDto> userResDtos = userService.getAllUsers(pageable);

        if (!userResDtos.isEmpty()) {
            response.setStatus(StatusConstants.SUCCESS);
            response.setData(userResDtos);
            response.setStatusCode(200);
            response.setMessage(messageSource.getMessage("fetch_success", null, locale));
        } else {
            response.setMessage(messageSource.getMessage("fetch_failed", null, locale));
        }

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));

    }

    @Operation(summary = "Get user", description = "Fetch user by id.")
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable("userId") Long userId) {
        List<String> details = new ArrayList<>();
        ApiResponse response = new ApiResponse(StatusConstants.FAILED, 400, AppConstant.DEFAULT_ERROR, null, null, null);

        UserResDto userResDtos = userService.getUserById(userId);
        if (userResDtos.getUserId() != 0) {
            response.setData(userResDtos);
            response.setStatus(StatusConstants.SUCCESS);
            response.setStatusCode(200);
            response.setMessage(messageSource.getMessage("fetch_success", null, locale));
        } else {
            response.setMessage(messageSource.getMessage("fetch_failed", null, locale));
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @Operation(summary = "Add user", description = "Save a new user")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserReqDto userReqDto) {
        List<String> details = new ArrayList<>();
        ApiResponse response = new ApiResponse(StatusConstants.FAILED, 400, AppConstant.DEFAULT_ERROR, null, null, null);

        try {
            UserResDto userResDto = userService.createUser(userReqDto);
            if (userResDto.getUserId() > 0) {
                response.setStatus(StatusConstants.SUCCESS);
                response.setStatusCode(200);
                response.setMessage(messageSource.getMessage("creation_success", null, locale));
                response.setData(userResDto);
            } else {
                response.setMessage(messageSource.getMessage("creation_failed", null, locale));
            }
        } catch (Exception e) {
            response.setErrorMessage(e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));

    }

    @Operation(summary = "User log in", description = "User log in")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(
            @RequestBody LoginReqDto loginReqDto,
            HttpServletRequest request) {
        List<String> details = new ArrayList<>();
        ApiResponse response = new ApiResponse(StatusConstants.FAILED, 400, AppConstant.DEFAULT_ERROR, null, null, null);

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginReqDto.getUsername(), loginReqDto.getPassword()));
            if (authentication.isAuthenticated()) {
                LoginTokenDto loginTokenDto = userService.login(loginReqDto, request);
                if (loginTokenDto.getToken() != null) {
                    response.setStatus(StatusConstants.SUCCESS);
                    response.setStatusCode(200);
                    response.setMessage(messageSource.getMessage("login_success", null, locale));
                    response.setData(loginTokenDto);
                } else {
                    response.setMessage(messageSource.getMessage("user_not_active", null, locale));
                }
            } else {
                response.setMessage(messageSource.getMessage("username_password_not_matched", null, locale));
            }
        } catch (Exception e) {
            response.setErrorMessage(e.getMessage());
            response.setMessage(messageSource.getMessage("login_failed", null, locale));
            response.setStatusCode(400);
        }

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));

    }


    @Operation(summary = "Update user", description = "Update user by userId")
    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable Long userId,
            @RequestBody UserReqDto userReqDto) {
        List<String> details = new ArrayList<>();
        ApiResponse response = new ApiResponse(StatusConstants.FAILED, 400, AppConstant.DEFAULT_ERROR, null, null, null);

        try {
            UserResDto userResDto = userService.updateUser(userId, userReqDto);
            if (userResDto.getUserId() > 0) {
                response.setStatusCode(200);
                response.setData(userResDto);
                response.setStatus(StatusConstants.SUCCESS);
                response.setMessage(messageSource.getMessage("update_success", new Object[]{userId}, locale));
            } else {
                response.setMessage(messageSource.getMessage("update_failed", new Object[]{userId}, locale));
            }
        } catch (Exception e) {
            response.setErrorMessage(e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @Operation(summary = "Change password", description = "Change user password. Old password must match.")
    @PatchMapping("{userId}/change-password")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> changePassword(
            @ValidOldPassword(id = "userId") @Valid @RequestBody PasswordChangeDto passwordChangeDto,
            @PathVariable("userId") Long userId) {

        List<String> details = new ArrayList<>();
        ApiResponse response = new ApiResponse(StatusConstants.FAILED, 400, AppConstant.DEFAULT_ERROR, null, null, null);

        if (passwordChangeDto.getNewPassword().equals(passwordChangeDto.getNewPasswordAgain())) {
            UserResDto userResDto = userService.changePassword(passwordChangeDto, userId);
            if (userResDto.getUserId() > 0) {
                response.setStatus(StatusConstants.SUCCESS);
                response.setStatusCode(200);
                response.setMessage(messageSource.getMessage("password_change_success", null, locale));
            } else {
                response.setMessage(messageSource.getMessage("password_change_failed", null, locale));
            }
        } else {
            response.setMessage(messageSource.getMessage("password_not_matched", null, locale));
            response.setStatusCode(400);
        }

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));

    }

}
