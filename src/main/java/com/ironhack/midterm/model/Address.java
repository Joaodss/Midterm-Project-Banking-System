package com.ironhack.midterm.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Address {

  @NotBlank
  private String streetAddress;

  @NotBlank
  private String postalCode;

  @NotBlank
  private String city;

  @NotBlank
  private String country;


  // ======================================== Override Methods ========================================
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
    Address address = (Address) o;
    return Objects.equals(streetAddress, address.streetAddress)
        && Objects.equals(postalCode, address.postalCode)
        && Objects.equals(city, address.city)
        && Objects.equals(country, address.country);
  }

  @Override
  public int hashCode() {
    return Objects.hash(streetAddress,
        postalCode,
        city,
        country);
  }


}
