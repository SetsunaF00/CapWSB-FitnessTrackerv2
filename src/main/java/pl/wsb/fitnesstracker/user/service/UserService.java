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

    // STARE FUNKCJONALNOŚCI — LABOWE DTO

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

    // MAPOWANIE DTO
    private UserDetailsDto mapToDetailsDto(User user) {
        return new UserDetailsDto(user.getId(), user.getFirstName(), user.getLastName(), user.getBirthdate(), user.getEmail());
    }

    // NOWE FUNKCJONALNOŚCI DLA TESTÓW INTEGRACYJNYCH

    // pobierz pełną listę użytkowników (cała encja User)
    public List<User> getAllUsersEntities() {
        return userRepository.findAll();
    }

    // pobierz uproszczoną listę użytkowników (na potrzeby /v1/users/simple)
    public List<UserSimpleDto> getAllUsersSimple() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserSimpleDto(user.getFirstName(), user.getLastName()))
                .collect(Collectors.toList());
    }

    // pobierz użytkownika po ID (pełna encja User)
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    // pobierz użytkowników po emailu (pełna encja UserWithEmailDto dla listy)
    public List<UserEmailDto> getUserByEmail(String email) {
        return userRepository.findByEmailContainingIgnoreCase(email)
                .stream()
                .map(user -> new UserEmailDto(user.getId(), user.getEmail()))
                .collect(Collectors.toList());
    }

    // pobierz użytkowników starszych niż podana data
    public List<User> getUsersOlderThan(LocalDate date) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getBirthdate().isBefore(date))
                .collect(Collectors.toList());
    }

    // zapis nowego użytkownika (pełna encja User)
    public void createUserEntity(User user) {
        userRepository.save(user);
    }

    // aktualizacja użytkownika (pełna encja User)
    public void updateUserEntity(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setBirthdate(updatedUser.getBirthdate());
        existingUser.setEmail(updatedUser.getEmail());

        userRepository.save(existingUser);
    }
}
