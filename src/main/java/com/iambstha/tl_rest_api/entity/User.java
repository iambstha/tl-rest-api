package com.iambstha.tl_rest_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.iambstha.tl_rest_api.domain.BaseEntity;
import com.iambstha.tl_rest_api.entity.enums.general.Gender;
import com.iambstha.tl_rest_api.entity.enums.user.UserRole;
import com.iambstha.tl_rest_api.entity.enums.user.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "tl_users", uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "email", unique = true)
    @Email(message = "Email should be valid email", regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")
    private String email;

    @Column(name = "is_email_verified")
    private Boolean isEmailVerified = false;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    @NotEmpty(message = "First name is required")
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty(message = "Last name is required")
    private String lastName;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_phone_verified")
    private Boolean isPhoneVerified = false;

    @JsonIgnore
    @Column(name = "last_password_changed_date")
    private Timestamp lastPasswordChangedDate;

    @JsonIgnore
    @Column(name = "last_password_changed_by")
    private Integer lastPasswordChangedBy;

    @JsonIgnore
    @Column(name = "last_login")
    private Timestamp lastLogin;

    @JsonIgnore
    @Column(name = "no_of_failed_logins")
    private Integer noOfFailedLogins = 0;

    @JsonIgnore
    @Column(name = "password_reset_code")
    private String passwordResetCode;

    @JsonIgnore
    @Column(name = "password_reset_code_ts")
    private Timestamp passwordResetCodeTs;

    @Column(name = "is_two_fa_enabled")
    private Boolean isTwoFaEnabled = false;

    @Column(name = "status")
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.INACTIVE;

    @Column(name = "profile_image")
    private String profileImage;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Blog> blogs;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @NotNull(message = "User role cannot be null")
    private UserRole role = UserRole.USER;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority(role.toString()));
        return Collections.singleton(role);
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
