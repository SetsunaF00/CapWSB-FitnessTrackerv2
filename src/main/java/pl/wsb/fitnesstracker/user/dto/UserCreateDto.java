
package pl.wsb.fitnesstracker.user.dto;

import java.time.LocalDate;

public record UserCreateDto(String firstName, String lastName, LocalDate birthdate, String email) { }
