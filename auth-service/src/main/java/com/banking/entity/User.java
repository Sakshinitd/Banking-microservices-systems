package com.banking.entity;

import com.banking.constant.UserRole;
import com.banking.constant.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;
    @Column(unique = true,  nullable = false)
    @Email
    private String email;
    private String passwordHash;
    @Column(nullable = false)
    private String fullName;
    private Set<UserRole> roles;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus=UserStatus.ACTIVE;
    @CreatedDate
    private LocalDateTime createdOn;
}
