package com.ironhack.midterm.account.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "third_party")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThirdParty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @NotBlank
    @Column(name = "name")
    private String name;

    // TODO JA - Study how to setup hashed key.
    @NotNull
    @NotBlank
    @Column(name = "hashed_key")
    private String hashedKey;


}
