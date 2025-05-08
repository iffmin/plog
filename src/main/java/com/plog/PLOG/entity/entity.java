package com.plog.PLOG.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class entity {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;
    private String username;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRoleType role;

}
