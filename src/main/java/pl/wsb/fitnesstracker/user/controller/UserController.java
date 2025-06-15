package pl.wsb.fitnesstracker.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.user.dto.*;
import pl.wsb.fitnesstracker.user.service.UserService;
import pl.wsb.fitnesstracker.user.api.User; // <-- zakładam, że masz również swój model User w api

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/users")  // <-- zmieniamy bazowy path na v1 (żeby pasowało do testów)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /v1/users - zwraca pełną listę użytkowników (dla testów)
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsersEntities());
    }

    // GET /v1/users/simple - zwraca uproszczoną listę użytkowników (firstName, lastName)
    @GetMapping("/simple")
    public ResponseEntity<List<UserSimpleDto>> getAllSimpleUsers() {
        return ResponseEntity.ok(userService.getAllUsersSimple());
    }

    // GET /v1/users/{id} - zwraca szczegóły użytkownika po ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserEntityById(id));
    }

    // GET /v1/users/email?email= - zwraca użytkownika po emailu
    @GetMapping("/email")
    public ResponseEntity<List<UserEmailDto>> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    // GET /v1/users/older/{date} - zwraca użytkowników starszych niż podana data
    @GetMapping("/older/{date}")
    public ResponseEntity<List<User>> getUsersOlderThan(@PathVariable("date") LocalDate date) {
        return ResponseEntity.ok(userService.getUsersOlderThan(date));
    }

    // DELETE /v1/users/{id} - usuwa użytkownika
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // POST /v1/users - tworzy użytkownika
    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody User user) {
        userService.createUserEntity(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // PUT /v1/users/{id} - aktualizuje użytkownika
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody User user) {
        userService.updateUserEntity(id, user);
        return ResponseEntity.ok().build();
    }

    // STARE ENDPOINTY (z labów, zostawiamy je na innym path)
    @GetMapping("/api/users")
    public ResponseEntity<List<UserSummaryDto>> getAllUsersOld() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/api/users/{id}")
    public ResponseEntity<UserDetailsDto> getUserByIdOld(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/api/users")
    public ResponseEntity<UserDetailsDto> createUserOld(@RequestBody UserCreateDto createDto) {
        return ResponseEntity.ok(userService.createUser(createDto));
    }

    @PutMapping("/api/users/{id}")
    public ResponseEntity<UserDetailsDto> updateUserOld(@PathVariable Long id, @RequestBody UserUpdateDto updateDto) {
        return ResponseEntity.ok(userService.updateUser(id, updateDto));
    }

    @GetMapping("/api/users/search")
    public ResponseEntity<List<UserEmailDto>> searchByEmailOld(@RequestParam String email) {
        return ResponseEntity.ok(userService.searchByEmail(email));
    }

    @GetMapping("/api/users/age")
    public ResponseEntity<List<UserSummaryDto>> searchByMinAgeOld(@RequestParam int minAge) {
        return ResponseEntity.ok(userService.searchByMinAge(minAge));
    }
}
