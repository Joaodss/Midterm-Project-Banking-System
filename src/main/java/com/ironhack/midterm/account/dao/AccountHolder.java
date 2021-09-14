package com.ironhack.midterm.account.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Entity
@Table(name = "account_holder")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountHolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @NotBlank
    @Column(name = "name")
    private String name;

    @NotNull
    @Past
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // TODO JA - check if not null applies only here
    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "streetAddress", column = @Column(name = "pa_street", nullable = false)),
            @AttributeOverride(name = "postalCode", column = @Column(name = "pa_postal_code", nullable = false)),
            @AttributeOverride(name = "city", column = @Column(name = "pa_city", nullable = false)),
            @AttributeOverride(name = "country", column = @Column(name = "pa_country", nullable = false)),
    })
    private Address primaryAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "streetAddress", column = @Column(name = "ma_street")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "ma_postal_code")),
            @AttributeOverride(name = "city", column = @Column(name = "ma_city")),
            @AttributeOverride(name = "country", column = @Column(name = "ma_country")),
    })
    private Address mailingAddress;


    // TODO JA - apply mapping

}
