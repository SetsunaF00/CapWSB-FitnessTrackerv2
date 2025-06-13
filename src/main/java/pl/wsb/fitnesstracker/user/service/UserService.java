
package pl.wsb.fitnesstracker.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.dto.*;
import pl.wsb.fitnesstracker.user.repository.ExtendedUserRepository;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final ExtendedUserRepository userRepository;

    public List<UserSummaryDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserSummaryDto(user.getId(), user.getFirstName()))
                .collect(Collectors.toList());
    }

    public UserDetailsDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        return mapToDetailsDto(user);
    }

    public UserDetailsDto createUser(UserCreateDto createDto) {
        User user = new User(createDto.firstName(), createDto.lastName(), createDto.birthdate(), createDto.email());
        User savedUser = userRepository.save(user);
        return mapToDetailsDto(savedUser);
    }

    public UserDetailsDto updateUser(Long id, UserUpdateDto updateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        user.setFirstName(updateDto.firstName());
        user.setLastName(updateDto.lastName());
        user.setBirthdate(updateDto.birthdate());
        user.setEmail(updateDto.email());
        User updatedUser = userRepository.save(user);
        return mapToDetailsDto(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found");
        }
        userRepository.deleteById(id);
    }

    public List<UserEmailDto> searchByEmail(String emailPart) {
        return userRepository.findByEmailContainingIgnoreCase(emailPart)
                .stream()
                .map(user -> new UserEmailDto(user.getId(), user.getEmail()))
                .collect(Collectors.toList());
    }

    public List<UserSummaryDto> searchByMinAge(int minAge) {
        List<User> allUsers = userRepository.findAll();
        LocalDate today = LocalDate.now();
        return allUsers.stream()
                .filter(user -> Period.between(user.getBirthdate(), today).getYears() > minAge)
                .map(user -> new UserSummaryDto(user.getId(), user.getFirstName()))
                .collect(Collectors.toList());
    }

    private UserDetailsDto mapToDetailsDto(User user) {
        return new UserDetailsDto(user.getId(), user.getFirstName(), user.getLastName(), user.getBirthdate(), user.getEmail());
    }
}
