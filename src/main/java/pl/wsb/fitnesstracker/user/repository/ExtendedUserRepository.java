package pl.wsb.fitnesstracker.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wsb.fitnesstracker.user.api.User;

import java.util.List;

public interface ExtendedUserRepository extends JpaRepository<User, Long> {

    List<User> findByEmailContainingIgnoreCase(String emailPart);
}
