
package pl.wsb.fitnesstracker.user.dto;

import java.time.LocalDate;

public record UserDetailsDto(Long id, String firstName, String lastName, LocalDate birthdate, String email) { }
