package com.iambstha.tl_rest_api.resource;

import com.iambstha.tl_rest_api.domain.ApiResponse;
import com.iambstha.tl_rest_api.dto.*;
import com.iambstha.tl_rest_api.exception.AuthException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

        ApiResponse apiResponse = ApiResponse.builder()
                .data(userService.getAllUsers(pageable))
                .statusCode(200)
                .message(messageSource.getMessage("fetch_success", null, locale))
                .build();


        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);

    }

    @Operation(summary = "Get user", description = "Fetch user by id.")
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable("userId") Long userId) {

        ApiResponse apiResponse = ApiResponse.builder()
                .data(userService.getUserById(userId))
                .statusCode(200)
                .message(messageSource.getMessage("fetch_success", null, locale))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @Operation(summary = "Add user", description = "Save a new user")
    @PostMapping("/save")
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserReqDto userReqDto) {

        ApiResponse apiResponse = ApiResponse.builder()
                .data(userService.createUser(userReqDto))
                .statusCode(201)
                .message(messageSource.getMessage("creation_success", null, locale))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

    }

    @Operation(summary = "User log in", description = "User log in")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginReqDto loginReqDto, HttpServletRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginReqDto.getUsername(), loginReqDto.getPassword()));
            if (authentication.isAuthenticated()) {
                ApiResponse apiResponse = ApiResponse.builder()
                        .data(userService.login(loginReqDto, request))
                        .statusCode(200)
                        .message(messageSource.getMessage("login_success", null, locale))
                        .build();

                return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
            } else {
                throw new AuthException(messageSource.getMessage("password_not_matched", null, locale));
            }
        } catch (Exception e) {
            throw new AuthException(messageSource.getMessage("login_failed", null, locale));
        }

    }


    @Operation(summary = "Update user", description = "Update user by userId")
    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long userId, @RequestBody UserReqDto userReqDto) {

        ApiResponse apiResponse = ApiResponse.builder()
                .data(userService.updateUser(userId, userReqDto))
                .statusCode(200)
                .message(messageSource.getMessage("update_success", null, locale))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);

    }

    @Operation(summary = "Change password", description = "Change user password. Old password must match.")
    @PatchMapping("{userId}/change-password")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse> changePassword(
            @ValidOldPassword(id = "userId") @Valid @RequestBody PasswordChangeDto passwordChangeDto,
            @PathVariable("userId") Long userId) {

        ApiResponse apiResponse = ApiResponse.builder()
                .data(userService.changePassword(passwordChangeDto, userId))
                .statusCode(200)
                .message(messageSource.getMessage("password_change_success", null, locale))
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);

    }

}
