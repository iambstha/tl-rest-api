package com.iambstha.tl_rest_api.validator.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD, ElementType.TYPE,PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = OldPasswordValidator.class)
@Documented
public @interface ValidOldPassword {

    String id() default "";
    String message() default "Invalid old password";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
