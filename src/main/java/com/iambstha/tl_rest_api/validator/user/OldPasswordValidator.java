package com.iambstha.tl_rest_api.validator.user;

import com.iambstha.tl_rest_api.dto.PasswordChangeDto;
import com.iambstha.tl_rest_api.entity.User;
import com.iambstha.tl_rest_api.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
public class OldPasswordValidator implements ConstraintValidator<ValidOldPassword, PasswordChangeDto> {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private Boolean valid;
    private String id;
    private String message;


    @Override
    public void initialize(ValidOldPassword constraintAnnotation) {
        this.message = constraintAnnotation.message();
        this.id = constraintAnnotation.id();
        this.valid = true;
    }

    @Override
    public boolean isValid(PasswordChangeDto passwordChangeDto, ConstraintValidatorContext constraintValidatorContext) {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Map<?, ?> pathVariables = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String strId = (String) pathVariables.get(this.id);
        if (passwordChangeDto == null) {
            this.valid = false;
        } else {
            Optional<User> userOptional = userRepository.findById(Long.parseLong(strId));
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                if (bCryptPasswordEncoder.matches(passwordChangeDto.getOldPassword(), user.getPassword())) {
                    valid = true;
                } else {
                    valid = false;
                    constraintValidatorContext.disableDefaultConstraintViolation();
                    constraintValidatorContext.buildConstraintViolationWithTemplate(this.message).addConstraintViolation();
                }

            }
        }

        return this.valid;
    }
}
