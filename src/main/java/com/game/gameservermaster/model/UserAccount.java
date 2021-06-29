package com.game.gameservermaster.model;

import lombok.Getter;

import javax.persistence.*;
import java.util.List;

@Getter
@Entity
@Table(name = "UserAccount")
public class UserAccount {

    @Id @Column(nullable=false)
    private String username;
    @Column(nullable=false)
    private String password;

    @OneToMany(targetEntity=UserAccountChar.class, mappedBy = "userAccount", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    private List<UserAccountChar> userAccountCharList;
}