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

        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RecordNotFoundException("User " + username + " not found"));

        return userMapper.toDto(user);
    }

    public Page<UserResDto> getAllUsers(Pageable pageable) {

        Page<User> users = userRepository.findAll(pageable);

        return new PageImpl<>(
                users.getContent().stream().map(userMapper::toDto).collect(Collectors.toList()),
                pageable,
                users.getTotalElements());
    }

    public UserResDto getUserById(Long userId) {
        return userMapper.toDto(userRepository.findById(userId)
                .orElseThrow(() ->
                        new RecordNotFoundException("User with user id " + userId + " not found")));
    }

    public User getUserActualById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RecordNotFoundException("User with user id " + userId + " not found"));
    }

    public UserResDto createUser(UserReqDto userReqDto) {
        User user = userMapper.toEntityFromReq(userReqDto);
        user.setUsername(generateUniqueUsernameForUser());
        user.setPassword(bCryptPasswordEncoder.encode(userReqDto.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        user.setNoOfFailedLogins(0);

        if (UserUtil.isUserLoggedIn()) {
            user.setCreatedBy(UserUtil.getUserId());
        }
        user.setCreatedTs(GeneralUtil.getCurrentTs());
        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    private String generateUniqueUsernameForUser() {
        String username = AppConstant.USERNAME_PREFIX + TokenGeneratorUtil.generateNumericOtp(6);

        Optional<User> optionalUser = userRepository.findByUsername(username);

        while (optionalUser.isPresent()) {
            username = AppConstant.USERNAME_PREFIX + TokenGeneratorUtil.generateNumericOtp(6);
            optionalUser = userRepository.findByUsername(username);
        }

        return username;
    }

    public LoginTokenDto login(LoginReqDto loginReqDto, HttpServletRequest request) {

        Optional<User> optionalUser = userRepository.findByUsername(loginReqDto.getUsername());

        if (optionalUser.isEmpty()) {
            throw new RecordNotFoundException("User not found");
        }

        User user = optionalUser.get();

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new NotAllowedException("User is either inactive, banned or deleted");
        }

        if (!bCryptPasswordEncoder.matches(loginReqDto.getPassword(), user.getPassword())) {
            throw new NotAllowedException("Invalid credentials");
        }

        user.setLastLogin(GeneralUtil.getCurrentTs());
        userRepository.save(user);

        HashMap<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("user_id", user.getUserId());
        additionalClaims.put("username", user.getUsername());

        String token = jwtUtil.generateToken(userLoadService.loadUserByUsername(loginReqDto.getUsername()), additionalClaims);
        String rToken = jwtUtil.generateRefreshToken(userLoadService.loadUserByUsername(loginReqDto.getUsername()), additionalClaims);

        LoginTokenDto loginTokenDto = new LoginTokenDto();
        loginTokenDto.setUserId(user.getUserId());
        loginTokenDto.setToken(token);
        loginTokenDto.setFirstName(user.getFirstName());
        loginTokenDto.setLastName(user.getLastName());
        loginTokenDto.setUsername(user.getUsername());
        loginTokenDto.setRefreshToken(rToken);

        String hostAddress = request.getHeader("origin");

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

        return loginTokenDto;
    }

    public UserResDto changePassword(PasswordChangeDto passwordChangeDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RecordNotFoundException("User with user id " + userId + " not found.")
        );

        if (passwordChangeDto.getNewPassword() != null
                && passwordChangeDto.getNewPassword().equals(passwordChangeDto.getNewPasswordAgain())) {

            String hashedPassword = BCrypt.hashpw(passwordChangeDto.getNewPassword(), BCrypt.gensalt(10));
            user.setPassword(hashedPassword);

            user.setModifiedBy(UserUtil.getUserId());
            user.setModifiedTs(GeneralUtil.getCurrentTs());

            return userMapper.toDto(userRepository.save(user));
        }

        return null;
    }

    public UserResDto updateUser(Long userId, UserReqDto userReqDto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RecordNotFoundException("User with id " + userId + " not found."));

        userMapper.updateUserFromDto(userReqDto, existingUser);

        userMapper.updatePassword(existingUser, userReqDto.getPassword());

        if (!existingUser.getRole().equals(userReqDto.getRole())) {

            if (!UserUtil.isAdmin()) {
                throw new NotAllowedException("Only admins can update the role");
            }
            existingUser.setRole(userReqDto.getRole());
        }

        existingUser.setModifiedBy(UserUtil.getUserId());
        existingUser.setModifiedTs(GeneralUtil.getCurrentTs());

        return userMapper.toDto(userRepository.save(existingUser));
    }


}


