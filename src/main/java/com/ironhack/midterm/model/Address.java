package com.ironhack.midterm.model;

import lombok.*;

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
    if (o == null || getClass() != o.getClass()) return false;
    Address address = (Address) o;
    return getStreetAddress().equals(address.getStreetAddress()) && getPostalCode().equals(address.getPostalCode()) && getCity().equals(address.getCity()) && getCountry().equals(address.getCountry());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getStreetAddress(), getPostalCode(), getCity(), getCountry());
  }

}
