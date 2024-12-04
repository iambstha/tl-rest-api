package com.iambstha.tl_rest_api.service;

import com.iambstha.tl_rest_api.constant.AppConstant;
import com.iambstha.tl_rest_api.dto.*;
import com.iambstha.tl_rest_api.entity.RefreshToken;
import com.iambstha.tl_rest_api.entity.User;
import com.iambstha.tl_rest_api.entity.enums.user.UserStatus;
import com.iambstha.tl_rest_api.exception.NotAllowedException;
import com.iambstha.tl_rest_api.exception.RecordNotFoundException;
import com.iambstha.tl_rest_api.mapper.UserMapper;
import com.iambstha.tl_rest_api.repository.RefreshTokenRepository;
import com.iambstha.tl_rest_api.repository.UserRepository;
import com.iambstha.tl_rest_api.security.JwtUtil;
import com.iambstha.tl_rest_api.util.GeneralUtil;
import com.iambstha.tl_rest_api.util.TokenGeneratorUtil;
import com.iambstha.tl_rest_api.util.UserUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserLoadServiceImpl userLoadService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserResDto findByUsernameIgnoreCase(String username) {
        // Fetch the user by username (case-insensitive) and throw an exception if not found
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RecordNotFoundException("User " + username + " not found"));

        // Map the User entity to a UserResDto and return it
        return userMapper.toDto(user);
    }

    public Page<UserResDto> getAllUsers(Pageable pageable) {
        // Retrieve a paginated list of all users from the repository
        Page<User> users = userRepository.findAll(pageable);

        // Convert the list of User entities to a list of UserResDto, and return a PageImpl
        return new PageImpl<>(
                users.getContent().stream().map(userMapper::toDto).collect(Collectors.toList()),
                pageable,
                users.getTotalElements());
    }

    public UserResDto getUserById(Long userId) {
        // Fetch the user by ID and throw an exception if not found
        return userMapper.toDto(userRepository.findById(userId)
                .orElseThrow(() ->
                        new RecordNotFoundException("User with user id " + userId + " not found")));
    }

    public UserResDto createUser(UserReqDto userReqDto) {
        User user = userMapper.toEntityFromReq(userReqDto);
        user.setUsername(generateUniqueUsernameForUser());
        user.setPassword(bCryptPasswordEncoder.encode(userReqDto.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        user.setNoOfFailedLogins(0);

        // Set the creator information if a user is logged in
        if (UserUtil.isUserLoggedIn()) {
            user.setCreatedBy(UserUtil.getUserId());
        }
        user.setCreatedTs(GeneralUtil.getCurrentTs());
        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    private String generateUniqueUsernameForUser() {
        // Initial username generation
        String username = AppConstant.USERNAME_PREFIX + TokenGeneratorUtil.generateNumericOtp(6);

        // Check if the username already exists in the repository
        Optional<User> optionalUser = userRepository.findByUsername(username);

        // Continue generating new usernames until a unique one is found
        while (optionalUser.isPresent()) {
            username = AppConstant.USERNAME_PREFIX + TokenGeneratorUtil.generateNumericOtp(6);
            optionalUser = userRepository.findByUsername(username);
        }

        // Return the unique username
        return username;
    }

    public LoginTokenDto login(LoginReqDto loginReqDto, HttpServletRequest request) {
        // Retrieve the user by username from the repository
        Optional<User> optionalUser = userRepository.findByUsername(loginReqDto.getUsername());

        // Throw an exception if the user is not found
        if (optionalUser.isEmpty()) {
            throw new RecordNotFoundException("User not found");
        }

        User user = optionalUser.get();

        // Check if the user's status is active
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new NotAllowedException("User is either inactive, banned or deleted");
        }

        // Validate the password against the stored hash
        if (!bCryptPasswordEncoder.matches(loginReqDto.getPassword(), user.getPassword())) {
            throw new NotAllowedException("Invalid credentials");
        }

        // Update the user's last login timestamp and save the user
        user.setLastLogin(GeneralUtil.getCurrentTs());
        userRepository.save(user);

        // Create additional claims for the JWT
        HashMap<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("user_id", user.getUserId());
        additionalClaims.put("username", user.getUsername());

        // Generate the JWT and refresh token
        String token = jwtUtil.generateToken(userLoadService.loadUserByUsername(loginReqDto.getUsername()), additionalClaims);
        String rToken = jwtUtil.generateRefreshToken(userLoadService.loadUserByUsername(loginReqDto.getUsername()), additionalClaims);

        // Populate the LoginTokenDto with user and token information
        LoginTokenDto loginTokenDto = new LoginTokenDto();
        loginTokenDto.setUserId(user.getUserId());
        loginTokenDto.setToken(token);
        loginTokenDto.setFirstName(user.getFirstName());
        loginTokenDto.setLastName(user.getLastName());
        loginTokenDto.setUsername(user.getUsername());
        loginTokenDto.setRefreshToken(rToken);

        // Get the host address from the request header
        String hostAddress = request.getHeader("origin");

        // Save the refresh token details to the repository
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setRefreshToken(rToken);
        refreshToken.setCreatedTs(GeneralUtil.getCurrentTs());
        refreshToken.setCreatedBy(UserUtil.getUserId());
        refreshToken.setUserId(user.getUserId());
        refreshToken.setIpAddress(request.getRemoteAddr());
        refreshToken.setHostAddress(hostAddress == null ? "" : hostAddress);
        refreshToken.setUserAgent(request.getHeader("User-Agent"));
        refreshTokenRepository.save(refreshToken);

        // Return the populated LoginTokenDto
        return loginTokenDto;
    }

    public UserResDto changePassword(PasswordChangeDto passwordChangeDto, Long userId) {
        // Retrieve the user by their ID from the repository
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RecordNotFoundException("User with user id " + userId + " not found.")
        );

        // Check if the new password is provided and matches the confirmation password
        if (passwordChangeDto.getNewPassword() != null
                && passwordChangeDto.getNewPassword().equals(passwordChangeDto.getNewPasswordAgain())) {

            // Hash the new password and set it to the user
            String hashedPassword = BCrypt.hashpw(passwordChangeDto.getNewPassword(), BCrypt.gensalt(10));
            user.setPassword(hashedPassword);

            // Update the user modification details
            user.setModifiedBy(UserUtil.getUserId());
            user.setModifiedTs(GeneralUtil.getCurrentTs());

            // Save the updated user to the repository and return the DTO
            return userMapper.toDto(userRepository.save(user));
        }

        // Return null if the passwords do not match or are not provided
        return null;
    }

    public UserResDto updateUser(Long userId, UserReqDto userReqDto) {
        // Retrieve the existing user by their ID from the repository
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RecordNotFoundException("User with id " + userId + " not found."));

        // Update the existing user entity with the data from the DTO
        userMapper.updateUserFromDto(userReqDto, existingUser);

        // Update the password if provided in the DTO
        userMapper.updatePassword(existingUser, userReqDto.getPassword());

        // Check if the user role is being updated
        if (!existingUser.getRole().equals(userReqDto.getRole())) {
            // Ensure only admins are allowed to update the role
            if (!UserUtil.isAdmin()) {
                throw new NotAllowedException("Only admins can update the role");
            }
            existingUser.setRole(userReqDto.getRole());
        }

        // Update the user modification details
        existingUser.setModifiedBy(UserUtil.getUserId());
        existingUser.setModifiedTs(GeneralUtil.getCurrentTs());

        // Save the updated user back to the repository and return the DTO
        return userMapper.toDto(userRepository.save(existingUser));
    }


}


