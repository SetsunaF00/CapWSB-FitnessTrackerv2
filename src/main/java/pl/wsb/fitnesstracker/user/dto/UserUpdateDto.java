
package pl.wsb.fitnesstracker.user.dto;

import java.time.LocalDate;

public record UserUpdateDto(String firstName, String lastName, LocalDate birthdate, String email) { }
