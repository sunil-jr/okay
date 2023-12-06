package com.paat.NNS.entity.user;

import com.paat.NNS.entity.base.BaseEntity;
import com.paat.NNS.miscallenous.enums.UserType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String otp;
    private Long otpExpiry;

    private boolean verified = false;

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @ManyToOne
    private Group group;
}
