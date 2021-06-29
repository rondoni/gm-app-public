package com.game.gameservermaster.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "AccountCharacter")
public class UserAccountChar {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="char_id")
    private int charID;
    @Column(name="char_name", nullable=false)
    private String charName;
    @Column(nullable=false)
    private String username;

    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name="username", referencedColumnName="username", insertable=false, updatable=false, nullable=false)
    private UserAccount userAccount;
}
